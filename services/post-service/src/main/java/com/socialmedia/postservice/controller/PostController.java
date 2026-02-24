package com.socialmedia.postservice.controller;

import com.socialmedia.postservice.dto.CreatePostDto;
import com.socialmedia.postservice.dto.CursorPageResponse;
import com.socialmedia.postservice.dto.PostDto;
import com.socialmedia.postservice.dto.UpdatePostDto;
import com.socialmedia.postservice.security.AuthenticatedUser;
import com.socialmedia.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(
            @Valid @RequestBody CreatePostDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        var user = AuthenticatedUser.fromJwt(jwt);

        Long id = postService.createPost(dto, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        var post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CursorPageResponse<PostDto>> getUserPosts(
            @PathVariable String userId,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer pageSize) {
        if (pageSize <= 0)
            pageSize = 10;

        var posts = postService.getUserPosts(userId, cursor, pageSize);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @RequestBody UpdatePostDto dto,
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt) {
        var user = AuthenticatedUser.fromJwt(jwt);
        postService.updatePost(dto, postId, user.id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt) {
        AuthenticatedUser user = AuthenticatedUser.fromJwt(jwt);
        postService.deletePost(postId, user.id());
        return ResponseEntity.noContent().build();
    }
}
