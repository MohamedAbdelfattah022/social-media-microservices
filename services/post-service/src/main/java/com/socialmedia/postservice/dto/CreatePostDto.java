package com.socialmedia.postservice.dto;

import com.socialmedia.postservice.enums.PrivacySettings;
import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    private String content;
    private List<String> mediaUrls;
    private PrivacySettings privacy = PrivacySettings.PUBLIC;
}
