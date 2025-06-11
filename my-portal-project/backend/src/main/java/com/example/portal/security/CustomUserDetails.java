package com.example.portal.security;

import com.example.portal.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security가 사용하는 사용자 정보 객체
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안 함
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 안 함
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 안 함
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성화 상태
    }
}
