package com.example.portal.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final String email;
    private final String name;
    private final String provider;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(String email, String name, String provider, Map<String, Object> attributes) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return name;
    }
}