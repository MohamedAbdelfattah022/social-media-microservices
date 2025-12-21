package com.socialmedia.postservice.dto;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLikeDto {
    private Long userId;
}
