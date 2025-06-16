package com.example.portal.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "에러 발생 시간")
    private final LocalDateTime timestamp;

    @Schema(description = "HTTP 상태 코드")
    private final int status;

    @Schema(description = "에러 코드")
    private final String code;

    @Schema(description = "에러 메시지")
    private final String message;

    @Schema(description = "요청 경로")
    private final String path;

    @Schema(description = "상세 에러 정보")
    private final Map<String, String> errors;

    public static ErrorResponse of(int status, String code, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .code(code)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse of(int status, String code, String message, String path, Map<String, String> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .code(code)
                .message(message)
                .path(path)
                .errors(errors)
                .build();
    }
}