package com.socialmedia.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentCommentId;
    private String content;
    private Boolean isEdited;
    private Long likeCount;
    private Long replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
