package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Characteristic {
    private boolean unique;
    private String name;
    private String value;
}
