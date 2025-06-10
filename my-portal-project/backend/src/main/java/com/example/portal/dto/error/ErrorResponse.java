package com.example.portal.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
}