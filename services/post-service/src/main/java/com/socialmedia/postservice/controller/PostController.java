package com.socialmedia.postservice.controller;

import com.socialmedia.postservice.dto.*;
import com.socialmedia.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;


    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody CreatePostDto dto) {
        Long id = postService.createPost(dto);
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
            @PathVariable Long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam Integer pageSize
    ) {
        if (pageSize <= 0) pageSize = 10;

        var posts = postService.getUserPosts(userId, cursor, pageSize);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@RequestBody UpdatePostDto dto, @PathVariable Long postId) {
        postService.updatePost(dto, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
