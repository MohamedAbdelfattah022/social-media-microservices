package com.socialmedia.postservice.entity;

import com.socialmedia.postservice.enums.PrivacySettings;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "posts")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(
            name = "post_media_urls",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "media_url")
    private List<String> mediaUrls;

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
