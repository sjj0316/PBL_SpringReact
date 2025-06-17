package com.example.portal.service;

import com.example.portal.dto.FileMetadata;
import com.example.portal.dto.FileValidation;
import com.example.portal.dto.UploadOptimization;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class FileManagementServiceTest {

        @TempDir
        Path tempDir;

        private FileManagementService fileManagementService;
        private static final String TEST_FILE_NAME = "test.txt";
        private static final String TEST_CONTENT = "Hello, World!";
        private static final String TEST_CONTENT_TYPE = "text/plain";

        @BeforeEach
        void setUp() {
                // Mock FileStorageService 생성
                FileStorageService mockFileStorageService = new FileStorageService() {
                        private int fileIdCounter = 0;

                        @Override
                        public FileMetadata storeFile(MultipartFile file) throws IOException {
                                fileIdCounter++;
                                return FileMetadata.of("test-file-id-" + fileIdCounter, file.getOriginalFilename(),
                                                file.getContentType(), file.getSize(), "/test");
                        }

                        @Override
                        public org.springframework.core.io.Resource loadFileAsResource(String fileId)
                                        throws IOException {
                                return null;
                        }

                        @Override
                        public void deleteFile(String fileId) throws IOException {
                                // Mock implementation
                        }

                        @Override
                        public boolean exists(String fileId) {
                                return true;
                        }

                        @Override
                        public long getFileSize(String fileId) throws IOException {
                                return 100L;
                        }

                        @Override
                        public FileMetadata getFileMetadata(String fileId) {
                                return null;
                        }

                        @Override
                        public List<FileMetadata> getAllFiles() {
                                return new ArrayList<>();
                        }

                        @Override
                        public void restoreFile(String fileId) throws IOException {
                                // Mock implementation
                        }

                        @Override
                        public List<FileMetadata> searchFiles(String keyword) {
                                return new ArrayList<>();
                        }

                        @Override
                        public byte[] getFileContent(String fileId) throws IOException {
                                return new byte[0];
                        }

                        @Override
                        public void updateFileMetadata(FileMetadata metadata) {
                                // Mock implementation
                        }

                        @Override
                        public boolean isFileExists(String fileId) {
                                return true;
                        }

                        @Override
                        public FileValidation validateFile(String fileId) {
                                return null;
                        }

                        @Override
                        public UploadOptimization optimizeFile(String fileId) throws IOException {
                                return null;
                        }

                        @Override
                        public Path getFilePath(String fileId) throws IOException {
                                return tempDir.resolve(fileId);
                        }
                };

                fileManagementService = new FileManagementService(
                                mockFileStorageService,
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
                assertThat(stats).isNotNull();
                assertThat(stats.get("totalFiles")).isEqualTo(2);
                assertThat(stats.get("totalSize")).isEqualTo(10L);
                // 평균값 등 필요시 추가
        }
}