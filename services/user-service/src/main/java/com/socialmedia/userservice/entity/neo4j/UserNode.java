package com.socialmedia.userservice.entity.neo4j;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNode {
    @Id
    @Property("userId")
    private String userId;

    @Version
    private Long version;

    @Property("username")
    private String username;

    @Property("createdAt")
    private LocalDateTime createdAt;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> following = new HashSet<>();

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.INCOMING)
    private Set<UserNode> followers = new HashSet<>();
}