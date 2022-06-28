package com.ffanaticism.intruder.serviceprovider.repository;

import com.ffanaticism.intruder.serviceprovider.model.StoredImage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<StoredImage, UUID> {
    @EntityGraph(value = "image.characteristics")
    StoredImage findByUrl(String url);
}
