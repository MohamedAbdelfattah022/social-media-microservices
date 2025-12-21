package com.socialmedia.postservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long userId;
    private LocalDateTime likedAt;
}
