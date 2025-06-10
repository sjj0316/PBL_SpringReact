package com.example.portal.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.errorCode = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }
}