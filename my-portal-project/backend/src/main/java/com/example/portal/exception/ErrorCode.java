package com.example.portal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "Method not allowed"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "Entity not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "Internal server error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "Invalid type value"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access denied"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "U002", "Email is duplicated"),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "U003", "Nickname is duplicated"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "Invalid password"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U005", "Unauthorized"),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Post not found"),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "P002", "Post access denied"),
    ALREADY_LIKED(HttpStatus.CONFLICT, "P003", "Already liked"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "P004", "Like not found"),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CM001", "Comment not found"),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CM002", "Comment access denied"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "Category not found"),
    CATEGORY_DUPLICATION(HttpStatus.CONFLICT, "CT002", "Category is duplicated"),

    // File
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "File not found"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F002", "File upload failed"),
    FILE_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "파일 저장 중 오류가 발생했습니다."),

    // Token
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "Token not found"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T002", "Token has expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "T003", "Invalid token");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}