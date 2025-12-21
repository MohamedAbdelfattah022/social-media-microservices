package com.socialmedia.postservice.dto;

import com.socialmedia.postservice.enums.PrivacySettings;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    private String content;
    private List<@URL String> mediaUrls;
    private PrivacySettings privacy;
}