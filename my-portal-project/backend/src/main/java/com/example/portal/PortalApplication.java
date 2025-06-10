package com.example.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.portal.config.JwtConfig;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 포털 시스템 메인 애플리케이션
 * 
 * @author portal-team
 * @version 1.0.0
 * @since 2024.03
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
@EnableJpaAuditing
public class PortalApplication {
  public static void main(String[] args) {
    SpringApplication.run(PortalApplication.class, args);
  }
}

// ./mvnw clean install -U
// ./mvnw clean compile
// mvnw.cmd spring-boot:run
