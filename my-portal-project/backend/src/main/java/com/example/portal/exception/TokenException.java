package com.example.portal.exception;

import com.example.portal.exception.ErrorCode;
import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public TokenException(String message) {
        this.message = message;
        this.errorCode = ErrorCode.INVALID_TOKEN;
    }

    public TokenException(String message, ErrorCode errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public TokenException(String message, ErrorCode errorCode, String details) {
        super(details);
        this.message = message;
        this.errorCode = errorCode;
    }

    public static class TokenReuseException extends TokenException {
        public TokenReuseException() {
            super("Refresh token was reused");
        }
    }

    public static class TokenExpiredException extends TokenException {
        public TokenExpiredException() {
            super("Refresh token was expired");
        }
    }

    public static class TokenNotFoundException extends TokenException {
        public TokenNotFoundException() {
            super("Refresh token not found");
        }
    }
}