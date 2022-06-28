package com.ffanaticism.intruder.serviceprovider.service.impl;

import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import com.ffanaticism.intruder.serviceprovider.entity.FileEntity;
import com.ffanaticism.intruder.serviceprovider.entity.Image;
import com.ffanaticism.intruder.serviceprovider.model.StoredImage;
import com.ffanaticism.intruder.serviceprovider.model.StoredImageCharacteristic;
import com.ffanaticism.intruder.serviceprovider.repository.ImageRepository;
import com.ffanaticism.intruder.serviceprovider.service.ImageService;
import com.ffanaticism.intruder.serviceprovider.util.FileUtil;
import com.ffanaticism.intruder.serviceprovider.util.format.ImageFormat;
import com.ffanaticism.intruder.serviceprovider.util.mapper.ImageMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
public class DefaultImageService implements ImageService {
    private final static String CHARSET = "UTF-8";
    private final static int SIZE = 256;

    private final ServiceProviderProperty properties;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Autowired
    public DefaultImageService(ServiceProviderProperty properties, ImageRepository imageRepository, ImageMapper imageMapper) {
        this.properties = properties;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    @Async("servicePool")
    public CompletableFuture<Image> download(String url) {
        var image = Image.builder()
                .url(url)
                .build();

        image.updateCharacteristic(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC, String.valueOf(getAmountOfDownloads(url) + 1));
        image.setName(properties.getMediaFilename() + "." + ImageFormat.JPG_OUTPUT_FILE_FORMAT);

        try {
            save(image);
        } catch (Throwable e) {
            log.warn("Can not save image: {}", image.getUrl());
            e.printStackTrace();
        }

        try {
            log.info("Start download image. URL: {}", url);
            image.setInputStream(new URL(url).openStream());
        } catch (IOException e) {
            throw new CompletionException(e);
        }

        return CompletableFuture.completedFuture(image);
    }

    @Override
    @Async("servicePool")
    public CompletableFuture<Image> generateQr(String text) {
        BitMatrix matrix;
        try {
            var hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            matrix = new MultiFormatWriter()
                    .encode(new String(text.getBytes(CHARSET), CHARSET), BarcodeFormat.QR_CODE, SIZE, SIZE, hints);
        } catch (WriterException | UnsupportedEncodingException e) {
            throw new CompletionException(e);
        }

        Image image;
        try {
            var name = properties.getMediaFilename() + "-" + new Random().nextInt(Integer.MAX_VALUE) + 1;
            var path = properties.getMediaDirectoryPath() + name + "." + ImageFormat.JPG_OUTPUT_FILE_FORMAT;

            FileUtil.createDirectory(properties.getMediaDirectoryPath());
            var file = FileUtil.createFile(path);

            MatrixToImageWriter.writeToPath(
                    matrix,
                    ImageFormat.JPG_OUTPUT_FILE_FORMAT,
                    Paths.get(path));

            image = Image.builder()
                    .name(name)
                    .inputStream(FileUtils.openInputStream(file))
                    .file(file)
                    .build();
        } catch (IOException e) {
            throw new CompletionException(e);
        }

        return CompletableFuture.completedFuture(image);
    }

    private void save(Image image) {
        var foundImage = imageRepository.findByUrl(image.getUrl());

        StoredImage updatedImage;
        if (foundImage != null) {
            updatedImage = foundImage;
            imageMapper.updatePatchEntity(imageMapper.toStoredImage(image), updatedImage);
        } else {
            updatedImage = imageMapper.toStoredImage(image);
        }

        imageRepository.save(updatedImage);
    }

    private int getAmountOfDownloads(String url) {
        var foundImage = imageRepository.findByUrl(url);
        return foundImage != null
                ? Integer.parseInt(foundImage.getCharacteristics().stream()
                .filter(e -> e.getName().equals(FileEntity.AMOUNT_OF_DOWNLOADS_CHARACTERISTIC))
                .findFirst()
                .map(StoredImageCharacteristic::getValue)
                .orElse("0"))
                : 0;
    }
}
