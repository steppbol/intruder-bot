package com.ffanaticism.intruder.serviceprovider.repository;

import com.ffanaticism.intruder.serviceprovider.model.StoredUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<StoredUser, UUID> {
    @Query(
            value = """
                    SELECT *
                    FROM users
                    INNER JOIN user_characteristics ON users.id = user_characteristics.user_id
                    WHERE CONCAT(user_characteristics.is_unique || '-' || user_characteristics.name || '-' || user_characteristics.value) in (:characteristics)
                    """,
            nativeQuery = true)
    StoredUser findByCharacteristics(List<String> characteristics);

    @NonNull
    @EntityGraph(value = "user.characteristics")
    List<StoredUser> findAll();
}
