package com.example.portal.exception;

public class FileStorageException extends BusinessException {
    private final String fileName;

    public FileStorageException(String message) {
        super(ErrorCode.FILE_STORAGE_ERROR, message);
        this.fileName = null;
    }

    public FileStorageException(String message, Throwable cause) {
        super(ErrorCode.FILE_STORAGE_ERROR, message, cause);
        this.fileName = null;
    }

    public FileStorageException(String message, String fileName) {
        super(ErrorCode.FILE_STORAGE_ERROR, message);
        this.fileName = fileName;
    }

    public FileStorageException(String message, String fileName, Throwable cause) {
        super(ErrorCode.FILE_STORAGE_ERROR, message, cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}