package com.socialmedia.minioservice.grpc;

import com.socialmedia.grpc.minio.*;
import com.socialmedia.minioservice.repository.FileMetadataRepository;
import com.socialmedia.minioservice.service.MinioService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileGrpcService extends FileServiceGrpc.FileServiceImplBase {
    private final FileMetadataRepository fileMetadataRepository;
    private final MinioService minioService;

    @Override
    public void validateFileIds(ValidateFileIdsRequest request, StreamObserver<ValidateFileIdsResponse> responseObserver) {
        log.debug("Validating {} file IDs", request.getFileIdsCount());

        List<String> invalidFileIds = new ArrayList<>();

        for (String fileIdStr : request.getFileIdsList()) {
            try {
                UUID fileId = UUID.fromString(fileIdStr);
                if (!fileMetadataRepository.existsById(fileId)) {
                    invalidFileIds.add(fileIdStr);
                }
            } catch (IllegalArgumentException e) {
                invalidFileIds.add(fileIdStr);
            }
        }

        ValidateFileIdsResponse response = ValidateFileIdsResponse.newBuilder()
                .setAllValid(invalidFileIds.isEmpty())
                .addAllInvalidFileIds(invalidFileIds)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getFileUrls(GetFileUrlsRequest request, StreamObserver<GetFileUrlsResponse> responseObserver) {
        log.debug("Getting URLs for {} file IDs", request.getFileIdsCount());

        List<FileUrlMapping> mappings = new ArrayList<>();

        for (String fileIdStr : request.getFileIdsList()) {
            try {
                UUID fileId = UUID.fromString(fileIdStr);
                fileMetadataRepository.findById(fileId).ifPresent(metadata -> {
                    String url = minioService.getPresignedUrl(fileId);
                    FileUrlMapping mapping = FileUrlMapping.newBuilder()
                            .setFileId(fileIdStr)
                            .setUrl(url)
                            .setContentType(metadata.getContentType() != null ? metadata.getContentType() : "")
                            .setOriginalFilename(metadata.getOriginalFilename())
                            .build();
                    mappings.add(mapping);
                });
            } catch (IllegalArgumentException e) {
                log.warn("Invalid file ID format: {}", fileIdStr);
            } catch (Exception e) {
                log.error("Error generating presigned URL for file {}: {}", fileIdStr, e.getMessage());
            }
        }

        GetFileUrlsResponse response = GetFileUrlsResponse.newBuilder()
                .addAllFileUrls(mappings)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteFiles(DeleteFilesRequest request, StreamObserver<DeleteFilesResponse> responseObserver) {
        log.debug("Deleting {} files", request.getFileIdsCount());

        List<String> failedFileIds = new ArrayList<>();

        for (String fileIdStr : request.getFileIdsList()) {
            try {
                UUID fileId = UUID.fromString(fileIdStr);
                minioService.deleteFile(fileId);
            } catch (Exception e) {
                log.error("Failed to delete file {}: {}", fileIdStr, e.getMessage());
                failedFileIds.add(fileIdStr);
            }
        }

        DeleteFilesResponse response = DeleteFilesResponse.newBuilder()
                .setSuccess(failedFileIds.isEmpty())
                .addAllFailedFileIds(failedFileIds)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
