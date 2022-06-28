package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public abstract class StreamedEntity {
    private String url;
    private InputStream inputStream;
    private InputStream coverStream;
    private String name;
    private boolean isDirect;

    public StreamedEntity(InputStream inputStream, InputStream coverStream, String name, String url, boolean isDirect) {
        this.url = url;
        this.inputStream = inputStream;
        this.coverStream = coverStream;
        this.name = name;
        this.isDirect = isDirect;
    }
}
