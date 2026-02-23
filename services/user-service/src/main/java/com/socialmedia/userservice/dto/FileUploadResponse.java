package com.socialmedia.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String id;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String message;
}
