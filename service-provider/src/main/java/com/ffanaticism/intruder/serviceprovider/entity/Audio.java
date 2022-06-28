package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Audio extends FileEntity {
    public static final String VIDEO_ID_CHARACTERISTIC = "videoId";
    public static final String TITLE_CHARACTERISTIC = "title";

    @Builder
    public Audio(File file, List<Characteristic> characteristics, InputStream inputStream, InputStream coverStream, String name, String url, boolean isDirect) {
        super(file, characteristics, inputStream, coverStream, name, url, isDirect);
    }
}
