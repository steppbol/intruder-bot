package com.ffanaticism.intruder.serviceprovider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(name = "image.characteristics",
        attributeNodes = @NamedAttributeNode("characteristics")
)
@Entity
@DynamicUpdate
@Table(name = "images")
public class StoredImage extends BaseEntity {
    @JsonProperty("url")
    @Column(name = "url", nullable = false)
    private String url;
    @JsonProperty("characteristics")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "image_id", nullable = false)
    private List<StoredImageCharacteristic> characteristics;
}
