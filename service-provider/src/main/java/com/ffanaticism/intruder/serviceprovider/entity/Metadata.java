package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metadata {
    private String title;
    private String artist;
}
