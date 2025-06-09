package com.example.portal.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final Map<String, Object> attributes;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(Map<String, Object> attributes, String email,
            Collection<? extends GrantedAuthority> authorities) {
        this.attributes = attributes;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return email;
    }
}