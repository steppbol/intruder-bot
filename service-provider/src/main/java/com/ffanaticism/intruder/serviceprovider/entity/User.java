package com.ffanaticism.intruder.serviceprovider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class User {
    private List<Characteristic> characteristics;

    public Characteristic getCharacteristic(String name) {
        Characteristic characteristic;
        if (characteristics != null) {
            characteristic = characteristics.stream()
                    .filter(e -> e.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        } else {
            characteristic = null;
        }

        return characteristic;
    }

    public void updateCharacteristic(String name, String value) {
        if (characteristics == null) {
            characteristics = new ArrayList<>();
        }
        for (var characteristic : characteristics) {
            if (characteristic.getName().equals(name)) {
                characteristic.setValue(value);
            }
        }
    }
}
