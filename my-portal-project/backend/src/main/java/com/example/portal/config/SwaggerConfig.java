package com.example.portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PBL 포털 시스템 API 문서")
                        .description("Spring Boot + React 기반 포털 시스템의 OpenAPI 명세서입니다.")
                        .version("v1.0.0"));
    }
}