package com.ffanaticism.intruder.serviceprovider.util;

import com.ffanaticism.intruder.serviceprovider.entity.SupportedPlatform;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PlatformUtil {
    private static final String UPPER_CASE_REGEX = "(?=\\p{Upper})";

    public static List<String> getAll() {
        return Arrays.stream(SupportedPlatform.values())
                .map(SupportedPlatform::getPlatform)
                .map(e -> e.split(UPPER_CASE_REGEX))
                .map(e -> Arrays.stream(e)
                        .map(StringUtils::capitalize)
                        .collect(Collectors.joining(" ")))
                .toList();
    }
}
