package com.socialmedia.postservice.service;

import com.socialmedia.grpc.interaction.PostInteractionCounts;
import com.socialmedia.postservice.consts.EventType;
import com.socialmedia.postservice.dto.CreatePostDto;
import com.socialmedia.postservice.dto.CursorPageResponse;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.dto.UpdatePostDto;
import com.socialmedia.postservice.dto.event.NotificationEvent;
import com.socialmedia.postservice.dto.event.NotificationEventType;
import com.socialmedia.postservice.dto.event.ResourceType;
import com.socialmedia.postservice.dto.projection.PostProjection;
import com.socialmedia.postservice.entity.Post;
import com.socialmedia.postservice.exception.ResourceNotFoundException;
import com.socialmedia.postservice.exception.ResourceOwnershipException;
import com.socialmedia.postservice.grpc.InteractionServiceClient;
import com.socialmedia.postservice.grpc.MinioServiceClient;
import com.socialmedia.postservice.mapper.EventFactory;
import com.socialmedia.postservice.mapper.PostMapper;
import com.socialmedia.postservice.repository.PostRepository;
import com.socialmedia.postservice.security.AuthenticatedUser;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final MessageProducer messageProducer;
    private final EventFactory eventFactory;
    private final InteractionServiceClient interactionServiceClient;
    private final MinioServiceClient minioServiceClient;

    @Transactional
    public Long createPost(CreatePostDto dto, AuthenticatedUser user) {
        validateFileIds(dto.getFileIds());

        var post = postMapper.toPost(dto, user);
        var savedPost = postRepository.save(post);
        var event = eventFactory.createEvent(savedPost, EventType.POST_CREATED);
        messageProducer.sendMessage(event);

        publishPostCreatedNotification(savedPost, user);

        return savedPost.getId();
    }

    public PostDto getPostById(Long postId) {
        if (!postRepository.existsById(postId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");

        var postProjection = postRepository.findByIdAndCountLikesAndComments(postId);

        List<UUID> fileIds = postMapper.getFileIds(postProjection);
        List<String> mediaUrls = minioServiceClient.getFileUrlsAsList(fileIds);

        Map<Long, PostInteractionCounts> countsMap = interactionServiceClient.getInteractionCounts(List.of(postId));
        PostInteractionCounts counts = countsMap.get(postId);

        if (counts != null) {
            return postMapper.toPostDto(postProjection, mediaUrls, counts.getLikeCount(), counts.getCommentCount());
        }
        return postMapper.toPostDto(postProjection, mediaUrls);
    }

    public CursorPageResponse<PostDto> getUserPosts(String userId, String cursor, Integer pageSize) {
        LocalDateTime cursorTime = null;
        Long lastId = null;

        if (!StringUtil.isNullOrEmpty(cursor)) {
            String[] parts = decodeCursor(cursor);
            cursorTime = LocalDateTime.parse(parts[0]);
            lastId = Long.parseLong(parts[1]);
        }

        List<PostProjection> projections = postRepository.findByUserIdWithCursor(userId, cursorTime, lastId, pageSize + 1);

        boolean hasNext = projections.size() > pageSize;
        if (hasNext) projections = projections.subList(0, pageSize);

        List<PostDto> posts = enrichWithInteractionCountsAndUrls(projections);

        String nextCursor = null;
        if (hasNext && !posts.isEmpty()) {
            var lastPost = posts.getLast();
            nextCursor = encodeCursor(lastPost.getCreatedAt(), lastPost.getId());
        }

        return CursorPageResponse.<PostDto>builder()
                .data(posts)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .pageSize(pageSize)
                .build();
    }

    public CursorPageResponse<PostDto> getPostsByUserIds(List<String> userIds, String cursor, int pageSize) {
        if (userIds == null || userIds.isEmpty()) {
            return CursorPageResponse.<PostDto>builder()
                    .data(List.of())
                    .nextCursor(null)
                    .hasNext(false)
                    .pageSize(pageSize)
                    .build();
        }
        LocalDateTime cursorTime = null;
        Long lastId = null;
        if (!StringUtil.isNullOrEmpty(cursor)) {
            String[] parts = decodeCursor(cursor);
            cursorTime = LocalDateTime.parse(parts[0]);
            lastId = Long.parseLong(parts[1]);
        }
        List<PostProjection> projections = postRepository.findByUserIdsWithCursor(
                userIds, cursorTime, lastId, pageSize + 1);
        boolean hasNext = projections.size() > pageSize;
        if (hasNext) projections = projections.subList(0, pageSize);
        List<PostDto> posts = enrichWithInteractionCountsAndUrls(projections);
        String nextCursor = null;
        if (hasNext && !posts.isEmpty()) {
            var lastPost = posts.getLast();
            nextCursor = encodeCursor(lastPost.getCreatedAt(), lastPost.getId());
        }
        return CursorPageResponse.<PostDto>builder()
                .data(posts)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .pageSize(pageSize)
                .build();
    }

    @Transactional
    public void updatePost(UpdatePostDto dto, Long postId, String userId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        verifyOwnership(post.getUserId(), userId);

        if (dto.getFileIds() != null) {
            validateFileIds(dto.getFileIds());
        }

        post.setEdited(true);
        post.setContent(dto.getContent() != null ? dto.getContent() : post.getContent());
        post.setPrivacy(dto.getPrivacy() != null ? dto.getPrivacy() : post.getPrivacy());
        post.setFileIds(dto.getFileIds() != null ? dto.getFileIds() : post.getFileIds());

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId, String userId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        verifyOwnership(post.getUserId(), userId);

        List<UUID> fileIds = post.getFileIds();

        postRepository.deleteById(postId);

        if (fileIds != null && !fileIds.isEmpty()) {
            var event = eventFactory.createPostDeletedEvent(postId, fileIds);
            messageProducer.sendPostDeletedMessage(event);
        }
    }


    private String encodeCursor(String createdAt, Long id) {
        String cursorString = createdAt + "," + id;
        return Base64.getUrlEncoder().encodeToString(cursorString.getBytes());
    }

    private String[] decodeCursor(String cursor) {
        String decoded = new String(Base64.getUrlDecoder().decode(cursor));
        return decoded.split(",");
    }

    private void verifyOwnership(String ownerId, String currentUserId) {
        if (!ownerId.equals(currentUserId))
            throw new ResourceOwnershipException("You do not have permission to perform this action");
    }

    private void validateFileIds(List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        List<String> invalidFileIds = minioServiceClient.getInvalidFileIds(fileIds);
        if (!invalidFileIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid file IDs: " + String.join(", ", invalidFileIds));
        }
    }

    private List<PostDto> enrichWithInteractionCountsAndUrls(List<PostProjection> projections) {
        if (projections.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = projections.stream()
                .map(PostProjection::getId)
                .toList();

        Map<Long, PostInteractionCounts> countsMap = interactionServiceClient.getInteractionCounts(postIds);

        List<UUID> allFileIds = projections.stream()
                .flatMap(p -> postMapper.getFileIds(p).stream())
                .distinct()
                .toList();

        Map<UUID, String> fileUrlMap = minioServiceClient.getFileUrls(allFileIds);

        return projections.stream()
                .map(projection -> {
                    List<UUID> fileIds = postMapper.getFileIds(projection);
                    List<String> mediaUrls = fileIds.stream()
                            .map(fileUrlMap::get)
                            .filter(Objects::nonNull)
                            .toList();

                    PostInteractionCounts counts = countsMap.get(projection.getId());
                    if (counts != null) {
                        return postMapper.toPostDto(projection, mediaUrls, counts.getLikeCount(), counts.getCommentCount());
                    }
                    return postMapper.toPostDto(projection, mediaUrls);
                })
                .toList();
    }

    private void publishPostCreatedNotification(Post post, AuthenticatedUser user) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("authorUsername", user.username());
        metadata.put("authorDisplayName", user.firstName() + " " + user.lastName());
        metadata.put("authorProfilePicture", user.profilePictureUrl());

        String preview = post.getContent() != null && post.getContent().length() > 100
                ? post.getContent().substring(0, 100) + "..."
                : post.getContent();
        metadata.put("postPreview", preview);

        NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(NotificationEventType.POST_CREATED)
                .sourceService("post-service")
                .timestamp(Instant.now())
                .actorUserId(user.id())
                .targetUserId(null)
                .resourceType(ResourceType.POST)
                .resourceId(post.getId().toString())
                .metadata(metadata)
                .build();

        messageProducer.publishNotificationEvent(event, "post.created");
    }
}
