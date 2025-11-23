package com.socialmedia.userservice.repository.neo4j;

import com.socialmedia.userservice.entity.neo4j.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGraphRepository extends Neo4jRepository<UserNode, Long> {

    @Query("MATCH (u:User {userId: $userId}) RETURN u")
    Optional<UserNode> findByUserId(Long userId);

    @Query("MATCH (u:User {userId: $userId})<-[:FOLLOWS]-(follower) RETURN follower")
    List<UserNode> findFollowers(Long userId);

    @Query("MATCH (u:User {userId: $userId})-[:FOLLOWS]->(following) RETURN following")
    List<UserNode> findFollowing(Long userId);

    @Query("MATCH (u:User {userId: $userId})<-[:FOLLOWS]-() RETURN COUNT(*)")
    Long countFollowers(Long userId);

    @Query("MATCH (u:User {userId: $userId})-[:FOLLOWS]->() RETURN COUNT(*)")
    Long countFollowing(Long userId);

    @Query("""
            MATCH (follower:User {userId: $followerId})
            MATCH (followee:User {userId: $followeeId})
            MERGE (follower)-[r:FOLLOWS]->(followee)
            SET r.followedAt = coalesce(r.followedAt, datetime())
            RETURN r
            """)
    void createFollowRelationship(Long followerId, Long followeeId);

    @Query("""
            MATCH (follower:User {userId: $followerId})-[r:FOLLOWS]->(followee:User {userId: $followeeId})
            DELETE r
            """)
    void deleteFollowRelationship(Long followerId, Long followeeId);

    @Query("""
            RETURN exists( (:User {userId: $followerId})-[:FOLLOWS]->(:User {userId: $followeeId}) )
            """)
    boolean isFollowing(Long followerId, Long followeeId);

    @Query("""
            MATCH (u:User {userId: $userId})
            DETACH DELETE u
            """)
    void deleteUserAndRelationships(Long userId);
}
