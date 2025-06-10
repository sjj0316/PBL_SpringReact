package com.example.portal.exception;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }

    public static class DuplicateEmailException extends UserException {
        public DuplicateEmailException() {
            super("이미 사용 중인 이메일입니다.");
        }
    }

    public static class InvalidPasswordException extends UserException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends UserException {
        public UserNotFoundException() {
            super("사용자를 찾을 수 없습니다.");
        }
    }
}