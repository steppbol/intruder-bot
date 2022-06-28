package com.ffanaticism.intruder.serviceprovider.service.impl;

import com.ffanaticism.intruder.serviceprovider.client.SongLinkClient;
import com.ffanaticism.intruder.serviceprovider.client.YouTubeClient;
import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import com.ffanaticism.intruder.serviceprovider.entity.FileEntity;
import com.ffanaticism.intruder.serviceprovider.entity.SupportedPlatform;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.exception.CastingException;
import com.ffanaticism.intruder.serviceprovider.exception.LargeMediaFileException;
import com.ffanaticism.intruder.serviceprovider.exception.MemoryLimitExceededException;
import com.ffanaticism.intruder.serviceprovider.exception.NotDownloadableException;
import com.ffanaticism.intruder.serviceprovider.exception.NotSupportedPlatformException;
import com.ffanaticism.intruder.serviceprovider.exception.SearchingException;
import com.ffanaticism.intruder.serviceprovider.model.StoredVideo;
import com.ffanaticism.intruder.serviceprovider.model.StoredVideoCharacteristic;
import com.ffanaticism.intruder.serviceprovider.repository.VideoRepository;
import com.ffanaticism.intruder.serviceprovider.service.VideoService;
import com.ffanaticism.intruder.serviceprovider.util.FileUtil;
import com.ffanaticism.intruder.serviceprovider.util.YoutubeUrlUtil;
import com.ffanaticism.intruder.serviceprovider.util.format.VideoFormat;
import com.ffanaticism.intruder.serviceprovider.util.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

@Slf4j
@Service
public class DefaultVideoService implements VideoService {
    private final YouTubeClient youTubeClient;
    private final SongLinkClient songLinkClient;
    private final VideoRepository videoRepository;
    private final ServiceProviderProperty properties;
    private final VideoMapper videoMapper;

    @Autowired
    public DefaultVideoService(YouTubeClient youTubeClient, SongLinkClient songLinkClient, VideoRepository videoRepository, ServiceProviderProperty properties, VideoMapper videoMapper) {
        this.youTubeClient = youTubeClient;
        this.songLinkClient = songLinkClient;
        this.videoRepository = videoRepository;
        this.properties = properties;
        this.videoMapper = videoMapper;
    }

    @Override
    @Async("servicePool")
    public CompletableFuture<List<Video>> search(String query, long limit) {
        List<Video> video;
        try {
            video = youTubeClient.search(query, limit);
        } catch (IOException | SearchingException e) {
            throw new CompletionException(e);
        }
        return CompletableFuture.completedFuture(video);
    }

    @Override
    @Async("servicePool")
    public CompletableFuture<Video> download(String url, String extension, float offset, float duration, Consumer<Integer> callback) {
        CompletableFuture<Video> future;
        try {
            var videoId = YoutubeUrlUtil.getVideoId(url);

            if (videoId.isBlank()) {
                videoId = YoutubeUrlUtil.getVideoId(songLinkClient.getPlatform(url).getLinksByPlatform().get(SupportedPlatform.YOUTUBE.getPlatform()).getUrl());
            }

            try {
                var videoDuration = youTubeClient.getVideoInformation(videoId).details().lengthSeconds();
                checkDownloadAvailability(videoDuration);
            } catch (LargeMediaFileException | MemoryLimitExceededException e) {
                throw new CompletionException(e);
            }

            Video video;
            var name = properties.getMediaFilename() + "-" + new Random().nextInt(Integer.MAX_VALUE) + 1;
            log.info("Start download video. URL: {}; name: {}", url, name);
            if (offset != 0 || duration != 0) {
                try {
                    video = download(videoId, extension, name, offset, duration, callback);
                } catch (NotDownloadableException | CastingException | IOException e) {
                    throw new CompletionException(e);
                }
            } else {
                try {
                    video = youTubeClient.downloadVideo(videoId, name, callback);
                } catch (NotDownloadableException e) {
                    throw new CompletionException(e);
                }
            }

            video.setCoverStream(FileUtil.getInputStream(properties.getMediaAlbumCoverPath()));
            video.setUrl(properties.getYouTubeUrl() + video.getCharacteristic(Video.VIDEO_ID_CHARACTERISTIC).getValue());
            video.updateCharacteristic(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC, String.valueOf(getAmountOfDownloads(video.getUrl()) + 1));

            try {
                save(video);
            } catch (Throwable e) {
                log.warn("Can not save video: {}", video.getUrl());
                e.printStackTrace();
            }

            future = CompletableFuture.completedFuture(video);
        } catch (NotSupportedPlatformException e) {
            future = download(url);
        }

        return future;
    }

    private CompletableFuture<Video> download(String url) {
        var name = properties.getMediaFilename() + "-" + new Random().nextInt(Integer.MAX_VALUE) + 1;
        var video = Video.builder()
                .name(name)
                .url(url)
                .build();

        try {
            log.info("Start download video. URL: {}; name: {}", url, name);
            video.setInputStream(new URL(url).openStream());
        } catch (IOException e) {
            throw new CompletionException(new NotDownloadableException());
        }

        video.updateCharacteristic(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC, String.valueOf(getAmountOfDownloads(url) + 1));

        try {
            save(video);
        } catch (Throwable e) {
            log.warn("Can not save video: {}", video.getUrl());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(video);
    }

    private Video download(String videoId, String extension, String name, float offset, float duration, Consumer<Integer> callback) throws NotDownloadableException, IOException, CastingException {
        var directory = FileUtil.createDirectory(properties.getMediaDirectoryPath());
        var video = youTubeClient.downloadVideo(videoId, directory, name, callback);

        File file;
        try {
            file = encode(video.getFile(), extension, name, offset, duration);
        } catch (EncoderException e) {
            throw new CastingException();
        }

        video.deleteFile();
        video.setFile(file);

        return video;
    }

    private File encode(File file, String extension, String name, float offset, float duration) throws EncoderException {
        var videoFile = new File(properties.getMediaDirectoryPath() + name + "-2." + extension);
        try {
            var encoder = new Encoder();
            var encodingAttributes = getVideoEncodingAttributes(extension, offset, duration);
            encoder.encode(new MultimediaObject(file), videoFile, encodingAttributes);
        } catch (EncoderException e) {
            FileUtil.delete(videoFile);
            throw e;
        }

        return videoFile;
    }

    private EncodingAttributes getVideoEncodingAttributes(String extension, float offset, float duration) {
        var encodingAttributes = new EncodingAttributes();
        if (extension.equals(VideoFormat.MP4_OUTPUT_FILE_FORMAT)) {
            encodingAttributes.setAudioAttributes(new AudioAttributes());
        }

        encodingAttributes.setOutputFormat(extension);
        encodingAttributes.setVideoAttributes(new VideoAttributes());

        if (offset != 0) {
            encodingAttributes.setOffset(offset);
        }
        if (duration != 0) {
            encodingAttributes.setDuration(duration);
        }

        return encodingAttributes;
    }

    private void checkDownloadAvailability(int duration) throws LargeMediaFileException, MemoryLimitExceededException {
        if (duration > properties.getMediaMaxDuration()) {
            throw new LargeMediaFileException();
        } else {
            var isEnoughSpace = FileUtil.checkFreeDirectorySpace(
                    FileUtil.createDirectory(properties.getMediaDirectoryPath()),
                    properties.getMediaDirectorySize()
            );

            if (!isEnoughSpace) {
                throw new MemoryLimitExceededException();
            }
        }
    }

    private void save(Video video) {
        var foundVideo = videoRepository.findByUrl(video.getUrl());

        StoredVideo updatedVideo;
        if (foundVideo != null) {
            updatedVideo = foundVideo;
            videoMapper.updatePatchEntity(videoMapper.toStoredVideo(video), updatedVideo);
        } else {
            updatedVideo = videoMapper.toStoredVideo(video);
        }

        videoRepository.save(updatedVideo);
    }

    private int getAmountOfDownloads(String url) {
        var foundVideo = videoRepository.findByUrl(url);
        return foundVideo != null
                ? Integer.parseInt(foundVideo.getCharacteristics().stream()
                .filter(e -> e.getName().equals(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC))
                .findFirst()
                .map(StoredVideoCharacteristic::getValue)
                .orElse("0"))
                : 0;
    }
}
