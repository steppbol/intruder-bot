package com.ffanaticism.intruder.serviceprovider.client.impl;

import com.ffanaticism.intruder.serviceprovider.client.YouTubeClient;
import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.Characteristic;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.exception.NotDownloadableException;
import com.ffanaticism.intruder.serviceprovider.exception.SearchingException;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.request.RequestVideoStreamDownload;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultYouTubeClient implements YouTubeClient {
    private static final List<String> PARTS_FOR_SEARCH = List.of("id", "snippet");
    private static final List<String> TYPES_FOR_SEARCH = List.of("video");
    private static final String FIELDS_FOR_SEARCH = "items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)";

    private static final Integer MAX_RETRIES = 10;

    private final ServiceProviderProperty properties;
    private final HttpTransport httpTransport;
    private final JsonFactory jacksonFactory;
    private final YoutubeDownloader downloader;

    @Autowired
    public DefaultYouTubeClient(ServiceProviderProperty properties) {
        this.properties = properties;
        this.httpTransport = new NetHttpTransport();
        this.jacksonFactory = new GsonFactory();

        downloader = new YoutubeDownloader();
        downloader.getConfig().setMaxRetries(MAX_RETRIES);
    }

    @Override
    public List<Video> search(String query, long limit) throws IOException, SearchingException {
        var youtube = new YouTube.Builder(httpTransport, jacksonFactory, request -> {
        }).setApplicationName(properties.getYouTubeApplicationName()).build();

        List<SearchResult> searchResults = new ArrayList<>();
        var search = youtube.search().list(PARTS_FOR_SEARCH);
        if (search != null) {
            search.setKey(properties.getYouTubeApiKey());
            search.setQ(query);
            search.setType(TYPES_FOR_SEARCH);
            search.setFields(FIELDS_FOR_SEARCH);
            search.setMaxResults(limit);

            var searchResponse = search.execute();
            if (searchResponse != null) {
                searchResults = searchResponse.getItems();
            } else {
                throw new SearchingException();
            }
        }

        return searchResults.stream()
                .map(searchResult -> {
                    List<Characteristic> characteristics = new ArrayList<>();
                    characteristics.add(new Characteristic(false, Video.THUMBNAIL_URL_CHARACTERISTIC, searchResult.getSnippet().getThumbnails().getDefault().getUrl()));
                    characteristics.add(new Characteristic(false, Video.DESCRIPTION_CHARACTERISTIC, searchResult.getSnippet().getDescription()));
                    characteristics.add(new Characteristic(false, Video.TITLE_CHARACTERISTIC, searchResult.getSnippet().getTitle()));

                    return Video.builder()
                            .url(properties.getYouTubeUrl() + searchResult.getId().getVideoId())
                            .characteristics(characteristics)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Video downloadVideo(String videoId, String name, Consumer<Integer> callback) throws NotDownloadableException {
        var info = getVideoInformation(videoId);

        var stream = new ByteArrayOutputStream();
        if (info.details().isDownloadable()) {
            var request = new RequestVideoStreamDownload(info.bestVideoWithAudioFormat(), stream)
                    .maxRetries(MAX_RETRIES)
                    .callback(new YoutubeProgressCallback<>() {
                        @Override
                        public void onDownloading(int progress) {
                            callback.accept(progress);
                        }

                        @Override
                        public void onFinished(Void empty) {
                            log.info("Video stream downloading is finished");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            log.info("Video stream downloading is failed");
                            throwable.printStackTrace();
                        }
                    });

            downloader.downloadVideoStream(request);
        } else {
            throw new NotDownloadableException();
        }

        List<Characteristic> characteristics = new ArrayList<>();
        characteristics.add(new Characteristic(false, Video.VIDEO_ID_CHARACTERISTIC, info.details().videoId()));
        characteristics.add(new Characteristic(false, Video.TITLE_CHARACTERISTIC, info.details().title()));

        return Video.builder()
                .name(name)
                .characteristics(characteristics)
                .inputStream(new ByteArrayInputStream(stream.toByteArray()))
                .build();
    }

    @Override
    public Video downloadVideo(String videoId, File directory, String name, Consumer<Integer> callback) throws NotDownloadableException, IOException {
        var info = getVideoInformation(videoId);

        File file;
        if (info.details().isDownloadable()) {
            var request = new RequestVideoFileDownload(info.bestVideoWithAudioFormat())
                    .maxRetries(MAX_RETRIES)
                    .saveTo(directory)
                    .callback(new YoutubeProgressCallback<>() {
                        @Override
                        public void onDownloading(int progress) {
                            callback.accept(progress);
                        }

                        @Override
                        public void onFinished(File videoInfo) {
                            log.info("Video downloading is finished. Path: {}", videoInfo.getPath());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            log.info("Video downloading is failed. Path: {}", directory.getPath());
                            throwable.printStackTrace();
                        }
                    })
                    .renameTo(name)
                    .overwriteIfExists(true);
            file = downloader.downloadVideoFile(request).data();
        } else {
            throw new NotDownloadableException();
        }

        List<Characteristic> characteristics = new ArrayList<>();
        characteristics.add(new Characteristic(false, Video.VIDEO_ID_CHARACTERISTIC, info.details().videoId()));
        characteristics.add(new Characteristic(false, Video.TITLE_CHARACTERISTIC, info.details().title()));

        return Video.builder()
                .name(name)
                .characteristics(characteristics)
                .file(file)
                .inputStream(FileUtils.openInputStream(file))
                .build();
    }

    @Override
    public Audio downloadAudio(String videoId, File directory, String name, Consumer<Integer> callback) throws NotDownloadableException, IOException {
        var info = getVideoInformation(videoId);

        File file;
        if (info.details().isDownloadable()) {
            var request = new RequestVideoFileDownload(info.bestAudioFormat())
                    .maxRetries(MAX_RETRIES)
                    .saveTo(directory)
                    .callback(new YoutubeProgressCallback<>() {
                        @Override
                        public void onDownloading(int progress) {
                            callback.accept(progress);
                        }

                        @Override
                        public void onFinished(File videoInfo) {
                            log.info("Audio downloading is finished. Path: {}", videoInfo.getPath());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            log.info("Audio downloading is failed. Path: {}", directory.getPath());
                            throwable.printStackTrace();
                        }
                    })
                    .renameTo(name)
                    .overwriteIfExists(true);
            file = downloader.downloadVideoFile(request).data();
        } else {
            throw new NotDownloadableException();
        }

        List<Characteristic> characteristics = new ArrayList<>();
        characteristics.add(new Characteristic(false, Video.VIDEO_ID_CHARACTERISTIC, info.details().videoId()));
        characteristics.add(new Characteristic(false, Video.TITLE_CHARACTERISTIC, info.details().title()));

        return Audio.builder()
                .name(name)
                .characteristics(characteristics)
                .file(file)
                .inputStream(FileUtils.openInputStream(file))
                .build();
    }

    @Override
    public VideoInfo getVideoInformation(String videoId) {
        var request = new RequestVideoInfo(videoId);
        var response = downloader.getVideoInfo(request);

        return response.data();
    }
}

