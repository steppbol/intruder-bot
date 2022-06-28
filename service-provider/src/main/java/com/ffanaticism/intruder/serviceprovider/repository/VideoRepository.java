package com.ffanaticism.intruder.serviceprovider.repository;

import com.ffanaticism.intruder.serviceprovider.model.StoredVideo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<StoredVideo, UUID> {
    @EntityGraph(value = "video.characteristics")
    StoredVideo findByUrl(String url);
}
