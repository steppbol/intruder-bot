package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformEntity {
    private String entityUniqueId;
    private String userCountry;
    private String pageUrl;
    private Map<String, Entity> entitiesByUniqueId;
    private Map<String, Link> linksByPlatform;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {
        private String id;
        private String type;
        private String title;
        private String artistName;
        private String thumbnailUrl;
        private String thumbnailWidth;
        private String thumbnailHeight;
        private String apiProvider;
        private List<String> platforms;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Link {
        private String country;
        private String url;
        private String entityUniqueId;
        private String nativeAppUriMobile;
        private String nativeAppUriDesktop;
    }
}
