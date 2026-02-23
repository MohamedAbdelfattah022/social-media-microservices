package com.socialmedia.userservice.client;

import com.socialmedia.userservice.config.FeignMultipartConfig;
import com.socialmedia.userservice.dto.FileUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "minio-service", url = "${services.minio.url}", configuration = FeignMultipartConfig.class)
public interface MinioFeignClient {

    @PostMapping(value = "/api/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileUploadResponse uploadFile(@RequestPart("file") MultipartFile file);

    @GetMapping("/api/files/{fileId}/presigned-url")
    String getPresignedUrl(@PathVariable("fileId") String fileId);

    @DeleteMapping("/api/files/delete/{fileId}")
    void deleteFile(@PathVariable("fileId") String fileId);
}
