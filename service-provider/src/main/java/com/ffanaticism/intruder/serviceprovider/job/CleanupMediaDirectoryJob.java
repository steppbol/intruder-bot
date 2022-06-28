package com.ffanaticism.intruder.serviceprovider.job;

import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import com.ffanaticism.intruder.serviceprovider.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class CleanupMediaDirectoryJob {
    private final ServiceProviderProperty properties;

    @Autowired
    public CleanupMediaDirectoryJob(ServiceProviderProperty properties) {
        this.properties = properties;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void cleanMediaDirectory() {
        var path = properties.getMediaDirectoryPath();
        try {
            FileUtil.cleanOldDirectory(new File(path), FileUtil.OLD_FILE_MINUTE_THRESHOLD);
        } catch (IOException e) {
            log.error("Error during directory cleaning. Path: {}", path);
            e.printStackTrace();
        }
    }
}
