package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JSON 역직렬화를 위해 필요
@AllArgsConstructor // 테스트 코드에서 사용할 생성자 자동 생성
public class RegisterRequestDto {
    private String username;
    private String password;
}
