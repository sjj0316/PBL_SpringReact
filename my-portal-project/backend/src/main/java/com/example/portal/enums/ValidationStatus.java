package com.example.portal.enums;

public enum ValidationStatus {
    PENDING, // 검증 대기 중
    VALIDATING, // 검증 진행 중
    VALID, // 검증 성공
    INVALID, // 검증 실패
    ERROR // 검증 중 오류 발생
}