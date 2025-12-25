package com.socialmedia.interactionservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;


@Builder
@Entity
@Getter @Setter
@Table(name = "likes")
@NoArgsConstructor @AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId;

    @NotNull
    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;
}