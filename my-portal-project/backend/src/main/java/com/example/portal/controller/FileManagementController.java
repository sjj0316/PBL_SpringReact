package com.example.portal.controller;

import com.example.portal.dto.FileMetadata;
import com.example.portal.service.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileManagementController {

    @Autowired
    private FileManagementService fileManagementService;

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", defaultValue = "/") String uploadPath) {
        try {
            FileMetadata metadata = fileManagementService.saveFile(file, uploadPath);
            return ResponseEntity.ok(metadata);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable String fileId) {
        FileMetadata metadata = fileManagementService.getFileMetadata(fileId);
        if (metadata != null) {
            return ResponseEntity.ok(metadata);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> listFiles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String accessLevel,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        List<FileMetadata> files = fileManagementService.listFiles(category, accessLevel, includeDeleted);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String fileId,
            @RequestParam(required = false) String reason) {
        try {
            fileManagementService.deleteFile(fileId, reason);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{fileId}/restore")
    public ResponseEntity<Void> restoreFile(@PathVariable String fileId) {
        fileManagementService.restoreFile(fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            Path filePath = fileManagementService.getFilePath(fileId);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                fileManagementService.incrementDownloadCount(fileId);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{fileId}/metadata")
    public ResponseEntity<Void> updateMetadata(
            @PathVariable String fileId,
            @RequestBody FileMetadata newMetadata) {
        fileManagementService.updateMetadata(fileId, newMetadata);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileMetadata>> searchFiles(@RequestParam String query) {
        List<FileMetadata> results = fileManagementService.searchFiles(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFileStatistics() {
        Map<String, Object> stats = fileManagementService.getFileStatistics();
        return ResponseEntity.ok(stats);
    }
}