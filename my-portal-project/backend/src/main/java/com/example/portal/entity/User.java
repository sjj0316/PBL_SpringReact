package com.example.portal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")  // Oracle 예약어 'user' 피하기 위해 복수형 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // ex: ROLE_USER
}
