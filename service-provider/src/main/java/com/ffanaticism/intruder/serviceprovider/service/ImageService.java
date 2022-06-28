package com.ffanaticism.intruder.serviceprovider.service;

import com.ffanaticism.intruder.serviceprovider.entity.Image;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface ImageService {
    @Async("servicePool")
    CompletableFuture<Image> download(String url);

    @Async("servicePool")
    CompletableFuture<Image> generateQr(String text);
}
