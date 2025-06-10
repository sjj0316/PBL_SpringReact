package com.example.portal.security;

import com.example.portal.entity.User;
import com.example.portal.exception.UnauthorizedException;
import com.example.portal.security.user.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new UnauthorizedException("유효하지 않은 사용자 정보입니다.");
        }

        return (UserPrincipal) principal;
    }
}