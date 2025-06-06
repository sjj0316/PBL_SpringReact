package com.example.portal.controller;

import com.example.portal.dto.LoginRequestDto;
import com.example.portal.dto.RegisterRequestDto;
import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 요청을 처리합니다.
     * 클라이언트에서 전달된 RegisterRequestDto를 기반으로 사용자 정보를 저장합니다.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) {
        // 사용자명 중복 체크
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 사용자명입니다.");
        }

        // 사용자 엔티티 생성 및 저장
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok("회원가입 완료");
    }

    /**
     * 로그인 요청을 처리합니다.
     * 사용자 인증 후 JWT 토큰을 생성하여 반환합니다.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        try {
            // 🔐 AuthenticationManager는 내부적으로 UserDetailsService를 사용하여
            // 사용자 인증을 수행합니다. 이때 username으로 DB에서 사용자를 조회하고,
            // 전달된 password가 DB의 암호화된 password와 일치하는지 검증합니다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(), // 사용자명
                            dto.getPassword() // 평문 비밀번호 (BCrypt 등으로 매칭됨)
                    ));

            // 🔐 인증 성공 후 JWT 토큰을 생성합니다.
            String token = jwtTokenProvider.createToken(authentication);

            // 🎉 토큰 반환 (추후 클라이언트는 이 토큰을 Authorization 헤더에 담아 요청함)
            // 수정된 부분: 명시적 타입 지정
            Map<String, String> response = Map.of("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // ❌ 인증 실패 시 (예: 사용자 없음, 비밀번호 불일치) 401 반환
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("잘못된 로그인 정보입니다.");
        }
    }

}
