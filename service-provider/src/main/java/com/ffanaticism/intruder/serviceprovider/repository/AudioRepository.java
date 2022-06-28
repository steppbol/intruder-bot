package com.ffanaticism.intruder.serviceprovider.repository;

import com.ffanaticism.intruder.serviceprovider.model.StoredAudio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AudioRepository extends JpaRepository<StoredAudio, UUID> {
    @EntityGraph(value = "audio.characteristics")
    StoredAudio findByUrl(String url);
}
