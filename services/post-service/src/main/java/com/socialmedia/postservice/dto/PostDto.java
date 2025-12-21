package com.socialmedia.postservice.dto;

import com.socialmedia.postservice.enums.PrivacySettings;
import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private Long userId;
    private String content;
    private PrivacySettings privacy;
    private List<String> mediaUrls;
    private boolean isEdited;
    private Long likeCount;
    private Long commentCount;
    private String createdAt;
    private String updatedAt;
}
