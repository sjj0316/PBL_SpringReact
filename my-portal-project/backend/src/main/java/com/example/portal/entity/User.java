package com.example.portal.entity;

// Lombok
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "profile_image")
    private String picture;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "ROLE_USER";
        }
    }

    public User update(String name, String picture) {
        this.username = name;
        this.picture = picture;
        return this;
    }

    public enum AuthProvider {
        LOCAL,
        GOOGLE,
        KAKAO,
        NAVER
    }

    public void updateNickname(String nickname) {
        this.name = nickname;
    }

    public String getNickname() {
        return this.name;
    }
}
