package com.example.portal.service;

import com.example.portal.config.UploadRateLimitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UploadRateLimitService {
    private final UploadRateLimitConfig config;
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    public void initializeRateLimiter(String uploadId, int rateLimit) {
        rateLimiters.put(uploadId, new RateLimiter(
                Math.min(Math.max(rateLimit, config.getMinRateLimit()), config.getMaxRateLimit()),
                config.getBurstSize(),
                config.getWindowSize()));
    }

    public boolean tryAcquire(String uploadId, int bytes) {
        if (!config.isEnabled()) {
            return true;
        }

        RateLimiter limiter = rateLimiters.get(uploadId);
        if (limiter == null) {
            limiter = new RateLimiter(
                    config.getDefaultRateLimit(),
                    config.getBurstSize(),
                    config.getWindowSize());
            rateLimiters.put(uploadId, limiter);
        }

        return limiter.tryAcquire(bytes);
    }

    public void updateRateLimit(String uploadId, int newRateLimit) {
        RateLimiter limiter = rateLimiters.get(uploadId);
        if (limiter != null) {
            limiter.updateRateLimit(
                    Math.min(Math.max(newRateLimit, config.getMinRateLimit()), config.getMaxRateLimit()));
        }
    }

    public void removeRateLimiter(String uploadId) {
        rateLimiters.remove(uploadId);
    }

    public int getCurrentRateLimit(String uploadId) {
        RateLimiter limiter = rateLimiters.get(uploadId);
        return limiter != null ? limiter.getRateLimit() : config.getDefaultRateLimit();
    }

    private static class RateLimiter {
        private final AtomicLong tokens;
        private final int burstSize;
        private final int windowSize;
        private volatile int rateLimit;
        private long lastRefillTime;

        public RateLimiter(int rateLimit, int burstSize, int windowSize) {
            this.rateLimit = rateLimit;
            this.burstSize = burstSize;
            this.windowSize = windowSize;
            this.tokens = new AtomicLong(burstSize);
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire(int bytes) {
            refillTokens();
            long currentTokens = tokens.get();
            if (currentTokens >= bytes) {
                return tokens.addAndGet(-bytes) >= 0;
            }
            return false;
        }

        private void refillTokens() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;
            if (timePassed >= windowSize) {
                long newTokens = (timePassed / windowSize) * rateLimit;
                tokens.set(Math.min(burstSize, newTokens));
                lastRefillTime = now;
            }
        }

        public void updateRateLimit(int newRateLimit) {
            this.rateLimit = newRateLimit;
        }

        public int getRateLimit() {
            return rateLimit;
        }
    }
}