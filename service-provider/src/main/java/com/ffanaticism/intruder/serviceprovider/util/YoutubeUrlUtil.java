package com.ffanaticism.intruder.serviceprovider.util;

import java.util.regex.Pattern;

public abstract class YoutubeUrlUtil {
    private static final Pattern URL_REGEX = Pattern.compile("^vi/|v=|/v/|youtu.be/|embed/$");

    public static String getVideoId(String url) {
        var videoId = "";
        var videoIds = url.split(URL_REGEX.pattern());
        if (videoIds.length > 1) {
            videoId = videoIds[1];
            var ampersandIndex = videoId.indexOf('&');

            if (ampersandIndex != -1) {
                videoId = videoId.substring(0, ampersandIndex);
            }
        }

        return videoId;
    }
}
