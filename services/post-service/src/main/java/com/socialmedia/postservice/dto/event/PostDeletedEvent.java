package com.socialmedia.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDeletedEvent {
    private Long postId;
    private List<String> fileIds;
    private LocalDateTime deletedAt;
}
