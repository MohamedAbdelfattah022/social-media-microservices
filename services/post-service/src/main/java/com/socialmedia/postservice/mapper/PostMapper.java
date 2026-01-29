package com.socialmedia.postservice.mapper;

import com.socialmedia.postservice.dto.CreatePostDto;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.dto.projection.PostProjection;
import com.socialmedia.postservice.entity.Post;
import com.socialmedia.postservice.enums.PrivacySettings;
import com.socialmedia.postservice.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

@Service
public class PostMapper {
    public Post toPost(CreatePostDto postDto, AuthenticatedUser user) {
        return Post.builder()
                .userId(user.id())
                .userName(user.username())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .profilePictureUrl(user.profilePictureUrl())
                .content(postDto.getContent())
                .mediaUrls(postDto.getMediaUrls())
                .privacy(postDto.getPrivacy())
                .build();
    }

    public PostDto toPostDto(PostProjection postProjection) {
        return PostDto.builder()
                .id(postProjection.getId())
                .userId(postProjection.getUserId())
                .userName(postProjection.getUserName())
                .firstName(postProjection.getFirstName())
                .lastName(postProjection.getLastName())
                .profilePictureUrl(postProjection.getProfilePictureUrl())
                .content(postProjection.getContent())
                .privacy(Enum.valueOf(PrivacySettings.class, postProjection.getPrivacy()))
                .mediaUrls(postProjection.getMediaUrls() != null ? java.util.Arrays.asList(postProjection.getMediaUrls()) : java.util.Collections.emptyList())
                .isEdited(postProjection.getIsEdited())
                .createdAt(postProjection.getCreatedAt().toString())
                .updatedAt(postProjection.getUpdatedAt().toString())
                .build();
    }

    public PostDto toPostDto(PostProjection postProjection, long likeCount, long commentCount) {
        PostDto dto = toPostDto(postProjection);
        dto.setLikeCount(likeCount);
        dto.setCommentCount(commentCount);
        return dto;
    }
}
