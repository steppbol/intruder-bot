package com.ffanaticism.intruder.serviceprovider.service;

import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface AudioService {
    @Async("servicePool")
    CompletableFuture<Audio> download(String url, String extension, String title, float offset, float duration, Consumer<Integer> callback);
}
