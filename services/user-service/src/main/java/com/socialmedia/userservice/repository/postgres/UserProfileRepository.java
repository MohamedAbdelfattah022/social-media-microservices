package com.socialmedia.userservice.repository.postgres;

import com.socialmedia.userservice.dto.UserProfileDto;
import com.socialmedia.userservice.entity.postgres.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query(value = """
            SELECT ue.id,
                   ue.first_name,
                   ue.last_name,
                   ue.username,
                   ue.email,
                   up.bio,
                   up.profile_picture_url
            FROM user_profiles up
            INNER JOIN user_entity ue ON up.id = ue.id
            WHERE LOWER(ue.username) LIKE LOWER(CONCAT('%', :query, '%'))
              AND ue.id != :excludeUserId
            ORDER BY ue.username ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<UserProfileDto> searchUsersByUsername(
            @Param("query") String query,
            @Param("excludeUserId") String excludeUserId,
            @Param("limit") int limit);

}
