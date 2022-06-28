package com.ffanaticism.intruder.serviceprovider.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public abstract class FileUtil {
    public static final int OLD_FILE_MINUTE_THRESHOLD = 15;
    private static final int SPACE_THRESHOLD = 85;

    public static File createDirectory(String path) {
        var file = new File(path);
        if (!file.exists()) {
            var created = file.mkdir();
            if (created) {
                log.info("Directory is created successfully. Path: {}", path);
            } else {
                log.error("Failed to create directory. Path: {}", path);
            }
        }

        return file;
    }

    public static File createFile(String path) {
        var file = new File(path);

        if (!file.exists()) {
            boolean created = false;
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                log.error("Error during creating file. Path: {}", path);
                e.printStackTrace();
            }

            if (created) {
                log.info("File is created successfully. Path: {}", path);
            } else {
                log.error("Failed to create file. Path: {}", path);
            }
        }

        return file;
    }

    public static InputStream getInputStream(String path) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.error("Error getting file input stream. Path: {}", path);
            e.printStackTrace();
        }

        return stream;
    }

    public static void delete(File file) {
        var path = file.getPath();
        var isSuccess = file.delete();
        if (isSuccess) {
            log.info("File is deleted successfully. Path: {}", path);
        } else {
            log.error("Failed deleting file. Path: {}", path);
        }
    }

    public static void cleanOldDirectory(File directory, int threshold) throws IOException {
        var files = directory.listFiles();
        if (files != null) {
            for (var file : files) {
                var attributes = Files.readAttributes(Paths.get(file.getPath()), BasicFileAttributes.class);
                var modifiedTime = attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault());
                var between = ChronoUnit.MINUTES.between(modifiedTime, ZonedDateTime.now().toInstant().atZone(ZoneId.systemDefault()));
                if (between > threshold) {
                    FileUtils.forceDelete(file);
                }
            }
        }
    }

    public static boolean checkFreeDirectorySpace(File directory, int maxSize) {
        var directorySize = FileUtils.sizeOfDirectory(directory) / 1024 / 1024;
        return !(directorySize / maxSize * 100 > SPACE_THRESHOLD);
    }
}