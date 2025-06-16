package com.example.portal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "upload.rate-limit")
@Getter
@Setter
public class UploadRateLimitConfig {
    private boolean enabled = true;
    private int defaultRateLimit = 1024 * 1024; // 1MB/s
    private int maxRateLimit = 10 * 1024 * 1024; // 10MB/s
    private int minRateLimit = 128 * 1024; // 128KB/s
    private int burstSize = 2 * 1024 * 1024; // 2MB
    private int windowSize = 1000; // 1초
    private boolean adaptiveRateLimit = true;
    private int adaptiveWindowSize = 5000; // 5초
    private double adaptiveFactor = 0.8; // 80% 대역폭 사용
}