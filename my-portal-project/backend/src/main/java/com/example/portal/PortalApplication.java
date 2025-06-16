package com.example.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.portal.config.JwtConfig;

/**
 * 포털 시스템 메인 애플리케이션
 * 
 * @author portal-team
 * @version 1.0.0
 * @since 2025.06
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class PortalApplication {
  public static void main(String[] args) {
    SpringApplication.run(PortalApplication.class, args);
  }
}

// ./mvnw clean install -U
// ./mvnw clean compile
// mvnw.cmd spring-boot:run
// 모든 수정이 완료되었으니 전체 테스트를 다시 실행해 줘. 실패하는 테스트가 있다면 구체적인 에러 메시지와 클래스/메서드 위치를 알려주고,
// 수정 가능한 경우 자동으로 수정해 줘.
