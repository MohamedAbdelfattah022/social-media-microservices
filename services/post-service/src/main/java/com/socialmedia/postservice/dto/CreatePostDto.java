package com.socialmedia.postservice.dto;

import com.socialmedia.postservice.enums.PrivacySettings;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    @Positive
    @NotNull
    private Long userId;

    private String content;
    private List<String> mediaUrls;
    private PrivacySettings privacy = PrivacySettings.PUBLIC;
}
