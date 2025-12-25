package com.socialmedia.interactionservice.dto;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {
    private String content;
}
