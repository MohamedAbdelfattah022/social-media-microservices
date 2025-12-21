package com.socialmedia.postservice.dto;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDto {
    private String content;
}
