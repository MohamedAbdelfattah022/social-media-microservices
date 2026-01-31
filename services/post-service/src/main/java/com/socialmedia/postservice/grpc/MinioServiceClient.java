package com.socialmedia.postservice.grpc;

import com.socialmedia.grpc.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceClient {
    private final FileServiceGrpc.FileServiceBlockingStub fileServiceStub;

    public boolean validateFileIds(List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return true;
        }

        log.debug("Validating {} file IDs", fileIds.size());

        List<String> fileIdStrings = fileIds.stream()
                .map(UUID::toString)
                .toList();

        ValidateFileIdsRequest request = ValidateFileIdsRequest.newBuilder()
                .addAllFileIds(fileIdStrings)
                .build();

        ValidateFileIdsResponse response = fileServiceStub.validateFileIds(request);

        if (!response.getAllValid()) {
            log.warn("Invalid file IDs detected: {}", response.getInvalidFileIdsList());
        }

        return response.getAllValid();
    }

    public List<String> getInvalidFileIds(List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> fileIdStrings = fileIds.stream()
                .map(UUID::toString)
                .toList();

        ValidateFileIdsRequest request = ValidateFileIdsRequest.newBuilder()
                .addAllFileIds(fileIdStrings)
                .build();

        ValidateFileIdsResponse response = fileServiceStub.validateFileIds(request);
        return response.getInvalidFileIdsList();
    }

    public Map<UUID, String> getFileUrls(List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyMap();
        }

        log.debug("Getting URLs for {} file IDs", fileIds.size());

        List<String> fileIdStrings = fileIds.stream()
                .map(UUID::toString)
                .toList();

        GetFileUrlsRequest request = GetFileUrlsRequest.newBuilder()
                .addAllFileIds(fileIdStrings)
                .build();

        GetFileUrlsResponse response = fileServiceStub.getFileUrls(request);

        return response.getFileUrlsList().stream()
                .collect(Collectors.toMap(
                        mapping -> UUID.fromString(mapping.getFileId()),
                        FileUrlMapping::getUrl
                ));
    }

    public List<String> getFileUrlsAsList(List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, String> urlMap = getFileUrls(fileIds);

        return fileIds.stream()
                .map(urlMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public void deleteFiles(List<UUID> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        log.debug("Deleting {} files", fileIds.size());

        List<String> fileIdStrings = fileIds.stream()
                .map(UUID::toString)
                .toList();

        DeleteFilesRequest request = DeleteFilesRequest.newBuilder()
                .addAllFileIds(fileIdStrings)
                .build();

        DeleteFilesResponse response = fileServiceStub.deleteFiles(request);

        if (!response.getSuccess()) {
            log.warn("Failed to delete some files: {}", response.getFailedFileIdsList());
        }
    }
}
