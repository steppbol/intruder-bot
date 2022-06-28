package com.ffanaticism.intruder.serviceprovider.service.impl;

import com.ffanaticism.intruder.serviceprovider.client.SongLinkClient;
import com.ffanaticism.intruder.serviceprovider.client.YouTubeClient;
import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.FileEntity;
import com.ffanaticism.intruder.serviceprovider.entity.PlatformEntity;
import com.ffanaticism.intruder.serviceprovider.entity.SupportedPlatform;
import com.ffanaticism.intruder.serviceprovider.exception.CastingException;
import com.ffanaticism.intruder.serviceprovider.exception.LargeMediaFileException;
import com.ffanaticism.intruder.serviceprovider.exception.MemoryLimitExceededException;
import com.ffanaticism.intruder.serviceprovider.exception.NotDownloadableException;
import com.ffanaticism.intruder.serviceprovider.exception.NotSupportedPlatformException;
import com.ffanaticism.intruder.serviceprovider.model.StoredAudio;
import com.ffanaticism.intruder.serviceprovider.model.StoredAudioCharacteristic;
import com.ffanaticism.intruder.serviceprovider.repository.AudioRepository;
import com.ffanaticism.intruder.serviceprovider.service.AudioService;
import com.ffanaticism.intruder.serviceprovider.util.FileUtil;
import com.ffanaticism.intruder.serviceprovider.util.YoutubeUrlUtil;
import com.ffanaticism.intruder.serviceprovider.util.format.AudioFormat;
import com.ffanaticism.intruder.serviceprovider.util.mapper.AudioMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

@Slf4j
@Service
public class DefaultAudioService implements AudioService {
    private static final String MP3_CODEC = "libmp3lame";
    private static final String OGG_CODEC = "libopus";

    private static final int AUDIO_BIT_RATE = 128000;
    private static final int AUDIO_CHANNELS = 2;
    private static final int AUDIO_SAMPLING_RATE = 48000;

    private static final String AUTHOR_TITLE_SPLITTER_REGEX = "[-–—一]";

    private static final String ARTIST_NOT_AVAILABLE_TEXT = "N/A";
    private static final String TITLE_NOT_AVAILABLE_TEXT = "N/A";

    private final AudioRepository audioRepository;
    private final ServiceProviderProperty properties;
    private final YouTubeClient youTubeClient;
    private final SongLinkClient songLinkClient;
    private final AudioMapper audioMapper;


    @Autowired
    public DefaultAudioService(AudioRepository audioRepository, ServiceProviderProperty properties, YouTubeClient youTubeClient, SongLinkClient songLinkClient, AudioMapper audioMapper) {
        this.audioRepository = audioRepository;
        this.properties = properties;
        this.youTubeClient = youTubeClient;
        this.songLinkClient = songLinkClient;
        this.audioMapper = audioMapper;
    }

    @Override
    @Async("servicePool")
    public CompletableFuture<Audio> download(String url, String extension, String title, float offset, float duration, Consumer<Integer> callback) {
        CompletableFuture<Audio> future;
        try {
            var videoId = YoutubeUrlUtil.getVideoId(url);
            var platform = songLinkClient.getPlatform(url);

            if (videoId.isBlank()) {
                videoId = YoutubeUrlUtil.getVideoId(platform.getLinksByPlatform().get(SupportedPlatform.YOUTUBE.getPlatform()).getUrl());
            }

            try {
                checkDownloadAvailability(youTubeClient.getVideoInformation(videoId).details().lengthSeconds());
            } catch (LargeMediaFileException | MemoryLimitExceededException e) {
                throw new CompletionException(e);
            }

            Audio audio;
            try {
                var name = properties.getMediaFilename() + "-" + new Random().nextInt(Integer.MAX_VALUE) + 1;
                log.info("Start download audio. URL: {}; name: {}", url, name);
                audio = download(videoId, extension, name, getTileFromPlatform(platform, title), offset, duration, callback);
            } catch (NotDownloadableException | IOException | CastingException e) {
                throw new CompletionException(e);
            }

            audio.setCoverStream(FileUtil.getInputStream(properties.getMediaAlbumCoverPath()));
            audio.setUrl(properties.getYouTubeUrl() + audio.getCharacteristic(Audio.VIDEO_ID_CHARACTERISTIC).getValue());
            audio.updateCharacteristic(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC, String.valueOf(getAmountOfDownloads(audio.getUrl()) + 1));
            audio.setDirect(false);

            try {
                save(audio);
            } catch (Throwable e) {
                log.warn("Can not save audio: {}", audio.getUrl());
                e.printStackTrace();
            }

            future = CompletableFuture.completedFuture(audio);
        } catch (NotSupportedPlatformException e) {
            future = download(url);
        }

        return future;
    }

    private CompletableFuture<Audio> download(String url) {
        var name = properties.getMediaFilename() + "-" + new Random().nextInt(Integer.MAX_VALUE) + 1;
        var audio = Audio.builder()
                .name(name)
                .url(url)
                .build();

        try {
            log.info("Start download audio. URL: {}; name: {}", url, name);
            audio.setInputStream(new URL(url).openStream());
        } catch (IOException e) {
            throw new CompletionException(new NotDownloadableException());
        }

        audio.setCoverStream(FileUtil.getInputStream(properties.getMediaAlbumCoverPath()));
        audio.updateCharacteristic(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC, String.valueOf(getAmountOfDownloads(url) + 1));
        audio.setDirect(true);

        try {
            save(audio);
        } catch (Throwable e) {
            log.warn("Can not save audio: {}", audio.getUrl());
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(audio);
    }

    private Audio download(String videoId, String format, String name, Title title, float offset, float duration, Consumer<Integer> callback) throws NotDownloadableException, IOException, CastingException {
        var directory = FileUtil.createDirectory(properties.getMediaDirectoryPath());
        var audio = youTubeClient.downloadAudio(videoId, directory, name, callback);

        File encodedFile;
        if (format.equals(AudioFormat.MP3_OUTPUT_FILE_FORMAT)) {
            try {
                encodedFile = encodeToMp3(audio, title, offset, duration);
            } catch (CastingException e) {
                audio.deleteFile();
                throw e;
            }
        } else if (format.equals(AudioFormat.OGG_OUTPUT_FILE_FORMAT)) {
            try {
                encodedFile = encodeToOgg(audio.getFile(), offset, duration);
            } catch (CastingException e) {
                audio.deleteFile();
                throw e;
            }
        } else {
            encodedFile = audio.getFile();
        }

        audio.deleteFile();
        audio.setFile(encodedFile);

        return audio;
    }

    private File encodeToMp3(Audio audio, Title title, float offset, float duration) throws CastingException {
        File encodedFile;
        try {
            encodedFile = encode(audio.getFile(), AudioFormat.MP3_OUTPUT_FILE_FORMAT, offset, duration);
        } catch (EncoderException e) {
            throw new CastingException();
        }

        AudioFile audioFile;
        try {
            audioFile = AudioFileIO.read(encodedFile);
            var tag = audioFile.getTag();

            tag.setField(tag.createField(FieldKey.ARTIST, title.artist()));
            tag.setField(tag.createField(FieldKey.TITLE, title.name()));

            var cover = AndroidArtwork.createArtworkFromFile(new File(properties.getMediaAlbumCoverPath()));

            tag.deleteArtworkField();
            tag.setField(cover);

            audioFile.commit();
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | CannotWriteException | InvalidAudioFrameException e) {
            FileUtil.delete(encodedFile);
            throw new CastingException();
        }

        return encodedFile;
    }

    private File encodeToOgg(File file, float offset, float duration) throws CastingException {
        File encodedFile;
        try {
            encodedFile = encode(file, AudioFormat.OGG_OUTPUT_FILE_FORMAT, offset, duration);
        } catch (EncoderException e) {
            throw new CastingException();
        }

        return encodedFile;
    }

    private File encode(File file, String extension, float offset, float duration) throws EncoderException {
        String fileName = file.getName();
        var encodedFile = new File(properties.getMediaDirectoryPath() + fileName.substring(0, fileName.lastIndexOf('.')) + "-2." + extension);
        try {
            var encoder = new Encoder();
            encoder.encode(new MultimediaObject(file), encodedFile, getEncodingAttributes(extension, offset, duration));
        } catch (EncoderException e) {
            FileUtil.delete(encodedFile);
            throw e;
        }

        return encodedFile;
    }

    private EncodingAttributes getEncodingAttributes(String extension, float offset, float duration) {
        var audioAttributes = new AudioAttributes();
        var encodingAttributes = new EncodingAttributes();
        if (extension.equals(AudioFormat.OGG_OUTPUT_FILE_FORMAT)) {
            audioAttributes.setCodec(OGG_CODEC);
            encodingAttributes.setOutputFormat(AudioFormat.OGG_OUTPUT_FILE_FORMAT);
        } else {
            audioAttributes.setCodec(MP3_CODEC);
            encodingAttributes.setOutputFormat(AudioFormat.MP3_OUTPUT_FILE_FORMAT);
        }

        audioAttributes.setBitRate(AUDIO_BIT_RATE);
        audioAttributes.setChannels(AUDIO_CHANNELS);
        audioAttributes.setSamplingRate(AUDIO_SAMPLING_RATE);

        encodingAttributes.setAudioAttributes(audioAttributes);

        if (offset != 0) {
            encodingAttributes.setOffset(offset);
        }
        if (duration != 0) {
            encodingAttributes.setDuration(duration);
        }

        return encodingAttributes;
    }

    private Title getTileFromPlatform(PlatformEntity platformEntity, String audioTitle) {
        var artist = ARTIST_NOT_AVAILABLE_TEXT;
        var title = TITLE_NOT_AVAILABLE_TEXT;

        if (!audioTitle.isBlank()) {
            var metaInformation = audioTitle.trim().split(AUTHOR_TITLE_SPLITTER_REGEX, 2);
            if (metaInformation.length > 1) {
                artist = metaInformation[0].trim();
                title = metaInformation[1].trim();
            } else if (metaInformation.length == 1) {
                title = metaInformation[0].trim();
            }
        } else {
            var entities = platformEntity.getEntitiesByUniqueId();
            var songPostfix = "_SONG";
            var videoPostfix = "_VIDEO";
            var entity = entities.entrySet().stream()
                    .filter(e ->
                            e.getKey().contains(SupportedPlatform.SPOTIFY.name().concat(songPostfix)) ||
                                    e.getKey().contains(SupportedPlatform.ITUNES.name().concat(songPostfix)) ||
                                    e.getKey().contains(SupportedPlatform.YANDEX.name().concat(songPostfix)) ||
                                    e.getKey().contains(SupportedPlatform.DEEZER.name().concat(songPostfix)) ||
                                    e.getKey().contains(SupportedPlatform.YOUTUBE_MUSIC.name().concat(songPostfix)) ||
                                    e.getKey().contains(SupportedPlatform.SOUNDCLOUD.name().concat(songPostfix)))
                    .findFirst()
                    .orElse(null);
            if (entity != null) {
                artist = StringEscapeUtils.unescapeHtml4(entity.getValue().getArtistName());
                title = StringEscapeUtils.unescapeHtml4(entity.getValue().getTitle());
            } else {
                entity = entities.entrySet().stream()
                        .filter(e -> e.getKey().contains(SupportedPlatform.YOUTUBE.name().concat(videoPostfix)))
                        .findFirst()
                        .orElse(null);
                if (entity != null) {
                    artist = StringEscapeUtils.unescapeHtml4(entity.getValue().getArtistName());
                    title = StringEscapeUtils.unescapeHtml4(entity.getValue().getTitle());
                }
            }
        }

        return new Title(title, artist);
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

    private void save(Audio audio) {
        var foundAudio = audioRepository.findByUrl(audio.getUrl());

        StoredAudio updatedAudio;
        if (foundAudio != null) {
            updatedAudio = foundAudio;
            audioMapper.updatePatchEntity(audioMapper.toStoredAudio(audio), updatedAudio);
        } else {
            updatedAudio = audioMapper.toStoredAudio(audio);
        }

        audioRepository.save(updatedAudio);
    }

    private int getAmountOfDownloads(String url) {
        var foundAudio = audioRepository.findByUrl(url);
        return foundAudio != null
                ? Integer.parseInt(foundAudio.getCharacteristics().stream()
                .filter(e -> e.getName().equals(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC))
                .findFirst()
                .map(StoredAudioCharacteristic::getValue)
                .orElse("0"))
                : 0;
    }

    private record Title(String name, String artist) {
    }
}
