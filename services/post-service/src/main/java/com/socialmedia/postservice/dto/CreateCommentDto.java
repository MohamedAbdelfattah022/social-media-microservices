package com.socialmedia.postservice.dto;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {
    private Long userId;
    private String content;
    private Long parentCommentId;
}
