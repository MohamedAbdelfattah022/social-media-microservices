package com.socialmedia.postservice.mapper;

import com.socialmedia.postservice.dto.CreatePostDto;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.dto.UserDto;
import com.socialmedia.postservice.dto.projection.PostProjection;
import com.socialmedia.postservice.entity.Post;
import com.socialmedia.postservice.enums.PrivacySettings;
import com.socialmedia.postservice.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class PostMapper {
    public Post toPost(CreatePostDto postDto, AuthenticatedUser user) {
        return Post.builder()
                .userId(user.id())
                .content(postDto.getContent())
                .fileIds(postDto.getFileIds())
                .privacy(postDto.getPrivacy())
                .build();
    }

    public PostDto toPostDto(PostProjection postProjection, UserDto user, List<String> mediaUrls) {
        return PostDto.builder()
                .id(postProjection.getId())
                .userId(postProjection.getUserId())
                .userName(user.username())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .profilePictureUrl(user.profilePictureUrl())
                .content(postProjection.getContent())
                .privacy(Enum.valueOf(PrivacySettings.class, postProjection.getPrivacy()))
                .mediaUrls(mediaUrls != null ? mediaUrls : Collections.emptyList())
                .isEdited(postProjection.getIsEdited())
                .createdAt(postProjection.getCreatedAt().toString())
                .updatedAt(postProjection.getUpdatedAt().toString())
                .build();
    }

    public PostDto toPostDto(PostProjection postProjection, UserDto user, List<String> mediaUrls, long likeCount,
            long commentCount) {
        PostDto dto = toPostDto(postProjection, user, mediaUrls);
        dto.setLikeCount(likeCount);
        dto.setCommentCount(commentCount);
        return dto;
    }

    public List<UUID> getFileIds(PostProjection projection) {
        if (projection.getFileIds() == null) {
            return Collections.emptyList();
        }
        return java.util.Arrays.asList(projection.getFileIds());
    }
}
