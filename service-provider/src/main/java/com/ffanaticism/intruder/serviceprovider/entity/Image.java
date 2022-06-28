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
public class Image extends FileEntity {
    @Builder
    public Image(File file, List<Characteristic> characteristics, InputStream inputStream, InputStream coverStream, String name, String url, boolean isDirect) {
        super(file, characteristics, inputStream, coverStream, name, url, isDirect);
    }
}
