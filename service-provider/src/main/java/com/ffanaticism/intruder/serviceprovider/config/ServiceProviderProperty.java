package com.ffanaticism.intruder.serviceprovider.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@Getter
@Setter
@EnableAsync
@Configuration
@PropertySource(value = "classpath:service-provider.yml", factory = YamlPropertySourceFactory.class)
public class ServiceProviderProperty {
    private final String youTubeUrl = "https://youtu.be/";
    private final String songLinkUrl = "https://api.song.link/v1-alpha.1";
    @Value("${youtube.application-name}")
    private String youTubeApplicationName;
    @Value("${youtube.api-key}")
    private String youTubeApiKey;
    @Value("${media.filename}")
    private String mediaFilename;
    @Value("${media.max-duration}")
    private int mediaMaxDuration;
    @Value("${media.album-cover-path}")
    private String mediaAlbumCoverPath;
    @Value("${media.directory-path}")
    private String mediaDirectoryPath;
    @Value("${media.directory-size}")
    private int mediaDirectorySize;
    @Value("${application.locale.tag}")
    private String localeTag;
    @Value("${download-pool.core-size}")
    private int downloadPoolCoreSize;
    @Value("${download-pool.max-size}")
    private int downloadPoolMaxSize;
    @Value("${download-pool.queue-capacity}")
    private int downloadPoolQueueCapacity;
}
