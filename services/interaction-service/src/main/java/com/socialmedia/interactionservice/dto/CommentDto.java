package com.socialmedia.interactionservice.dto;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private String userId;
    private String firstname;
    private String lastname;
    private String content;
    private Long parentCommentId;
    private Boolean isEdited;
    private Long likeCount;
    private Long replyCount;
    private String createdAt;
    private String updatedAt;
}
