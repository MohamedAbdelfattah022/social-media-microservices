package com.socialmedia.postservice.dto;

import com.socialmedia.postservice.enums.PrivacySettings;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    private String content;
    private List<UUID> fileIds;
    private PrivacySettings privacy;
}