package com.socialmedia.minioservice.listener;

import com.socialmedia.minioservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostDeletedEventListener {
    private final MinioService minioService;

    @RabbitListener(queues = "postDeletedQueue")
    public void handlePostDeleted(PostDeletedEvent event) {
        log.info("Received post deleted event for post ID: {}", event.getPostId());

        if (event.getFileIds() == null || event.getFileIds().isEmpty()) {
            log.debug("No files to delete for post ID: {}", event.getPostId());
            return;
        }

        for (String fileIdStr : event.getFileIds()) {
            try {
                UUID fileId = UUID.fromString(fileIdStr);
                minioService.deleteFile(fileId);
                log.info("Successfully deleted file {} for post {}", fileIdStr, event.getPostId());
            } catch (IllegalArgumentException e) {
                log.error("Invalid file ID format: {}", fileIdStr);
            } catch (Exception e) {
                log.error("Failed to delete file {} for post {}: {}",
                        fileIdStr, event.getPostId(), e.getMessage());
            }
        }
    }
}
