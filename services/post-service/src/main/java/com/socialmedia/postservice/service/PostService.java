package com.socialmedia.postservice.service;

import com.socialmedia.postservice.consts.EventType;
import com.socialmedia.postservice.dto.CreatePostDto;
import com.socialmedia.postservice.dto.CursorPageResponse;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.dto.UpdatePostDto;
import com.socialmedia.postservice.dto.projection.PostProjection;
import com.socialmedia.postservice.exception.ResourceNotFoundException;
import com.socialmedia.postservice.exception.ResourceOwnershipException;
import com.socialmedia.postservice.mapper.EventFactory;
import com.socialmedia.postservice.mapper.PostMapper;
import com.socialmedia.postservice.repository.PostRepository;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final MessageProducer messageProducer;
    private final EventFactory eventFactory;

    @Transactional
    public Long createPost(CreatePostDto dto, String userId) {
        var post = postMapper.toPost(dto, userId);
        var savedPost = postRepository.save(post);
        var event = eventFactory.createEvent(savedPost, EventType.POST_CREATED);
        messageProducer.sendMessage(event);

        return savedPost.getId();
    }

    public PostDto getPostById(Long postId) {
        if (!postRepository.existsById(postId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");

        var postProjection = postRepository.findByIdAndCountLikesAndComments(postId);
        return postMapper.toPostDto(postProjection);
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

        List<PostDto> posts = projections.stream()
                .map(postMapper::toPostDto)
                .toList();

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
        List<PostDto> posts = projections.stream()
                .map(postMapper::toPostDto)
                .toList();
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

    public void updatePost(UpdatePostDto dto, Long postId, String userId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        verifyOwnership(post.getUserId(), userId);

        post.setEdited(true);
        post.setContent(dto.getContent() != null ? dto.getContent() : post.getContent());
        post.setPrivacy(dto.getPrivacy() != null ? dto.getPrivacy() : post.getPrivacy());
        post.setMediaUrls(dto.getMediaUrls() != null ? dto.getMediaUrls() : post.getMediaUrls());

        postRepository.save(post);
    }

    public void deletePost(Long postId, String userId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        verifyOwnership(post.getUserId(), userId);

        postRepository.deleteById(postId);
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
}
