package com.example.portal.exception;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
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