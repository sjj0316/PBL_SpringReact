package com.example.portal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 인증 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // 리소스 관련 에러
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

    // 파일 관련 에러
    FILE_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 파일을 찾을 수 없습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),

    // 유효성 검사 에러
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다."),

    // 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}