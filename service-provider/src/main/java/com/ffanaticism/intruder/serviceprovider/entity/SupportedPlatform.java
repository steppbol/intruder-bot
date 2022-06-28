package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.Getter;

@Getter
public enum SupportedPlatform {
    SPOTIFY("spotify"),
    ITUNES("itunes"),
    APPLE_MUSIC("appleMusic"),
    YANDEX("yandex"),
    DEEZER("deezer"),
    SOUNDCLOUD("soundcloud"),
    YOUTUBE_MUSIC("youtubeMusic"),
    YOUTUBE("youtube"),
    AMAZON_MUSIC("amazonMusic"),
    AMAZON_STORE("amazonStore"),
    PANDORA("pandora"),
    TIDAL("tidal"),
    NAPSTER("napster"),
    SPINRILLA("spinrilla"),
    AUDIUS("audius"),
    GOOGLE("google"),
    GOOGLE_STORE("googleStore"),
    PRFIN("prfin");

    private final String platform;

    SupportedPlatform(String platform) {
        this.platform = platform;
    }
}
