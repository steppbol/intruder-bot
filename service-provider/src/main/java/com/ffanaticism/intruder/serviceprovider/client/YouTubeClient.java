package com.ffanaticism.intruder.serviceprovider.client;

import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.exception.NotDownloadableException;
import com.ffanaticism.intruder.serviceprovider.exception.SearchingException;
import com.github.kiulian.downloader.model.videos.VideoInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public interface YouTubeClient {
    List<Video> search(String query, long limit) throws IOException, SearchingException;

    Video downloadVideo(String videoId, String name, Consumer<Integer> callback) throws NotDownloadableException;

    Video downloadVideo(String videoId, File path, String name, Consumer<Integer> callback) throws NotDownloadableException, IOException;

    Audio downloadAudio(String videoId, File path, String name, Consumer<Integer> callback) throws NotDownloadableException, IOException;

    VideoInfo getVideoInformation(String videoId);
}
