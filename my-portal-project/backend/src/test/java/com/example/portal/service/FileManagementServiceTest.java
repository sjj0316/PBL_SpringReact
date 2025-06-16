package com.example.portal.service;

import com.example.portal.dto.FileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileManagementServiceTest {

    @TempDir
    Path tempDir;

    private FileManagementService fileManagementService;
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_CONTENT = "Hello, World!";
    private static final String TEST_CONTENT_TYPE = "text/plain";

    @BeforeEach
    void setUp() {
        fileManagementService = new FileManagementService(
                tempDir.toString(),
                "text/plain,image/jpeg,image/png",
                1048576L);
    }

    @Test
    void saveFile_ShouldCreateMetadata() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                TEST_FILE_NAME,
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                TEST_CONTENT.getBytes());

        // When
        FileMetadata metadata = fileManagementService.saveFile(file, "/test");

        // Then
        assertNotNull(metadata);
        assertEquals(TEST_FILE_NAME, metadata.getFileName());
        assertEquals(TEST_CONTENT_TYPE, metadata.getFileType());
        assertEquals(TEST_CONTENT.length(), metadata.getFileSize());
        assertEquals("/test", metadata.getUploadPath());
        assertFalse(metadata.isDeleted());
        assertEquals(0, metadata.getDownloadCount());
        assertEquals(0, metadata.getViewCount());
    }

    @Test
    void saveFile_ShouldThrowException_WhenFileIsEmpty() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile(
                TEST_FILE_NAME,
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                new byte[0]);

        // When & Then
        assertThrows(IOException.class, () -> fileManagementService.saveFile(emptyFile, "/test"));
    }

    @Test
    void getFileMetadata_ShouldReturnMetadata() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                TEST_FILE_NAME,
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                TEST_CONTENT.getBytes());
        FileMetadata savedMetadata = fileManagementService.saveFile(file, "/test");

        // When
        FileMetadata retrievedMetadata = fileManagementService.getFileMetadata(savedMetadata.getFileId());

        // Then
        assertNotNull(retrievedMetadata);
        assertEquals(savedMetadata.getFileId(), retrievedMetadata.getFileId());
        assertEquals(1, retrievedMetadata.getViewCount());
    }

    @Test
    void deleteFile_ShouldMarkAsDeleted() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                TEST_FILE_NAME,
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                TEST_CONTENT.getBytes());
        FileMetadata metadata = fileManagementService.saveFile(file, "/test");

        // When
        fileManagementService.deleteFile(metadata.getFileId(), "Test deletion");

        // Then
        FileMetadata deletedMetadata = fileManagementService.getFileMetadata(metadata.getFileId());
        assertTrue(deletedMetadata.isDeleted());
        assertEquals("Test deletion", deletedMetadata.getDeleteReason());
    }

    @Test
    void restoreFile_ShouldRestoreDeletedFile() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                TEST_FILE_NAME,
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                TEST_CONTENT.getBytes());
        FileMetadata metadata = fileManagementService.saveFile(file, "/test");
        fileManagementService.deleteFile(metadata.getFileId(), "Test deletion");

        // When
        fileManagementService.restoreFile(metadata.getFileId());

        // Then
        FileMetadata restoredMetadata = fileManagementService.getFileMetadata(metadata.getFileId());
        assertFalse(restoredMetadata.isDeleted());
        assertNull(restoredMetadata.getDeleteReason());
    }

    @Test
    void searchFiles_ShouldReturnMatchingFiles() throws IOException {
        // Given
        MultipartFile file1 = new MockMultipartFile(
                "test1.txt",
                "test1.txt",
                TEST_CONTENT_TYPE,
                "Hello".getBytes());
        MultipartFile file2 = new MockMultipartFile(
                "test2.txt",
                "test2.txt",
                TEST_CONTENT_TYPE,
                "World".getBytes());
        fileManagementService.saveFile(file1, "/test");
        fileManagementService.saveFile(file2, "/test");

        // When
        List<FileMetadata> results = fileManagementService.searchFiles("test1");

        // Then
        assertEquals(1, results.size());
        assertEquals("test1.txt", results.get(0).getFileName());
    }

    @Test
    void getFileStatistics_ShouldReturnCorrectStats() throws IOException {
        // Given
        MultipartFile file1 = new MockMultipartFile(
                "test1.txt",
                "test1.txt",
                TEST_CONTENT_TYPE,
                "Hello".getBytes());
        MultipartFile file2 = new MockMultipartFile(
                "test2.txt",
                "test2.txt",
                TEST_CONTENT_TYPE,
                "World".getBytes());
        fileManagementService.saveFile(file1, "/test");
        fileManagementService.saveFile(file2, "/test");

        // When
        Map<String, Object> stats = fileManagementService.getFileStatistics();

        // Then
        assertEquals(2L, stats.get("totalFiles"));
        assertEquals(10L, stats.get("totalSize"));
        assertEquals(0L, stats.get("deletedFiles"));
        assertEquals(0L, stats.get("publicFiles"));
    }
}