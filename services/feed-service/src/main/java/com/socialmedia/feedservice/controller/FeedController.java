package com.socialmedia.feedservice.controller;


import com.socialmedia.feedservice.constant.FeedConstants;
import com.socialmedia.feedservice.dto.CursorPageResponse;
import com.socialmedia.feedservice.dto.FeedItemDto;
import com.socialmedia.feedservice.security.AuthenticatedUser;
import com.socialmedia.feedservice.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Feed operations")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    @Operation(summary = "Get user feed", description = "Returns posts from users the current user follows")
    public ResponseEntity<CursorPageResponse<FeedItemDto>> getFeed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "" + FeedConstants.DEFAULT_PAGE_SIZE)
            @Min(value = FeedConstants.MIN_PAGE_SIZE, message = "Page size must be at least " + FeedConstants.MIN_PAGE_SIZE)
            @Max(value = FeedConstants.MAX_PAGE_SIZE, message = "Page size must not exceed " + FeedConstants.MAX_PAGE_SIZE)
            int pageSize,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AuthenticatedUser user = AuthenticatedUser.fromJwt(jwt);
        CursorPageResponse<FeedItemDto> feed = feedService.getFeed(user.id(), cursor, pageSize);
        return ResponseEntity.ok(feed);
    }
}
