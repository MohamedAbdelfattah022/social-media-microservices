package com.socialmedia.postservice.mapper;

import com.socialmedia.postservice.dto.CreatePostDto;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.dto.projection.PostProjection;
import com.socialmedia.postservice.entity.Post;
import com.socialmedia.postservice.enums.PrivacySettings;
import org.springframework.stereotype.Service;

@Service
public class PostMapper {
    public Post toPost(CreatePostDto postDto, String userId) {
        return Post.builder()
                .userId(userId)
                .content(postDto.getContent())
                .mediaUrls(postDto.getMediaUrls())
                .privacy(postDto.getPrivacy())
                .build();
    }

    public PostDto toPostDto(PostProjection postProjection) {
        return PostDto.builder()
                .id(postProjection.getId())
                .userId(postProjection.getUserId())
                .content(postProjection.getContent())
                .privacy(Enum.valueOf(PrivacySettings.class, postProjection.getPrivacy()))
                .mediaUrls(postProjection.getMediaUrls() != null ? java.util.Arrays.asList(postProjection.getMediaUrls()) : java.util.Collections.emptyList())
                .isEdited(postProjection.getIsEdited())
//                .likeCount(postProjection.getLikeCount())
//                .commentCount(postProjection.getCommentCount())
                .createdAt(postProjection.getCreatedAt().toString())
                .updatedAt(postProjection.getUpdatedAt().toString())
                .build();
    }
}
