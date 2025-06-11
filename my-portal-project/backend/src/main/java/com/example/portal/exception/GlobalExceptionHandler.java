package com.example.portal.exception;

import com.example.portal.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * 전역 예외 처리기
 * 애플리케이션에서 발생하는 모든 예외를 처리하고 적절한 HTTP 응답을 반환합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex,
                        HttpServletRequest request) {
                log.warn("UnauthorizedException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code("UNAUTHORIZED")
                                                .message(ex.getMessage())
                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                        HttpServletRequest request) {
                log.warn("ResourceNotFoundException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code("RESOURCE_NOT_FOUND")
                                                .message(ex.getMessage())
                                                .status(HttpStatus.NOT_FOUND.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(FileStorageException.class)
        public ResponseEntity<ErrorResponse> handleFileStorageException(FileStorageException ex,
                        HttpServletRequest request) {
                log.error("FileStorageException: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code("FILE_STORAGE_ERROR")
                                                .message(ex.getMessage())
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
                log.warn("BusinessException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code(ex.getErrorCode().getCode())
                                                .message(ex.getMessage())
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                log.warn("AccessDeniedException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code("ACCESS_DENIED")
                                                .message("접근 권한이 없습니다.")
                                                .status(HttpStatus.FORBIDDEN.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
                        HttpServletRequest request) {
                log.warn("BadCredentialsException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code("INVALID_CREDENTIALS")
                                                .message("잘못된 인증 정보입니다.")
                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
        public ResponseEntity<ErrorResponse> handleValidationException(Exception ex, HttpServletRequest request) {
                log.warn("ValidationException: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .code("VALIDATION_ERROR")
                                                .message("입력값이 올바르지 않습니다.")
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .path(request.getRequestURI())
                                                .build());
        }

        @ExceptionHandler(Exception.class)
        protected ResponseEntity<ErrorResponse> handleException(Exception e) {
                log.error("Exception", e);
                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build();
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}