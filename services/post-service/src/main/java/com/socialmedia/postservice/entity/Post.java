package com.socialmedia.postservice.entity;

import com.socialmedia.postservice.enums.PrivacySettings;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "post_file_ids", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "file_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UUID> fileIds;

    @Enumerated(EnumType.STRING)
    private PrivacySettings privacy = PrivacySettings.PUBLIC;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited = false;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
