package com.ffanaticism.intruder.serviceprovider.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "audio_characteristics")
public class StoredAudioCharacteristic extends BaseEntity {
    @JsonProperty("unique")
    @Column(name = "is_unique", nullable = false)
    private boolean unique;
    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;
    @JsonProperty("value")
    @Column(name = "value")
    private String value;
}
