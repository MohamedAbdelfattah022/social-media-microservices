package com.socialmedia.userservice.repository.postgres;

import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    @Query(value = """
            SELECT ue.id,
                   ue.first_name,
                   ue.last_name,
                   ue.username,
                   ue.email,
                   up.bio,
                   up.profile_picture_url
            FROM user_profiles up
            INNER JOIN user_entity ue
            ON up.id = ue.id
            WHERE up.id = :id
            """, nativeQuery = true)
    Optional<UserProfileDto> findProfileById(@Param("id") String id);

}
