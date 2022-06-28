package com.ffanaticism.intruder.serviceprovider.service;

import com.ffanaticism.intruder.serviceprovider.entity.Video;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface VideoService {
    @Async("servicePool")
    CompletableFuture<List<Video>> search(String query, long limit);

    @Async("servicePool")
    CompletableFuture<Video> download(String url, String extension, float offset, float duration, Consumer<Integer> callback);
}
