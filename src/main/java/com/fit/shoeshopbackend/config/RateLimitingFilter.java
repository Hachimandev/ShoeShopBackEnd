package com.fit.shoeshopbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Filter sử dụng thuật toán Token Bucket theo IP.
 *
 * Giới hạn theo từng nhóm endpoint:
 *  - /api/auth/**   : 5 requests / phút  (chống brute force login)
 *  - /api/orders/** : 10 requests / phút (chống spam checkout)
 *  - /api/ai/**     : 15 requests / phút (giới hạn AI chat tốn tài nguyên)
 */
@Component
@EnableScheduling
public class RateLimitingFilter extends OncePerRequestFilter {

    // ----------------------------------------------------------------
    // Cấu hình giới hạn cho từng loại endpoint
    // ----------------------------------------------------------------
    private enum RateLimitPolicy {
        AUTH(5, 5.0 / 60),          // 5 tokens burst, nạp lại ~5 req/phút
        CHECKOUT(10, 10.0 / 60),    // 10 tokens burst, nạp lại ~10 req/phút
        AI(15, 15.0 / 60);          // 15 tokens burst, nạp lại ~15 req/phút

        final int capacity;
        final double refillPerSecond;

        RateLimitPolicy(int capacity, double refillPerSecond) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
        }
    }

    // ----------------------------------------------------------------
    // Token Bucket: mỗi (IP + policy) có 1 bucket riêng
    // ----------------------------------------------------------------
    private static class TokenBucket {
        private final int capacity;
        private final double refillPerSecond;
        private double tokens;
        private Instant lastRefillTime;
        private Instant lastAccessTime;

        TokenBucket(RateLimitPolicy policy) {
            this.capacity = policy.capacity;
            this.refillPerSecond = policy.refillPerSecond;
            this.tokens = policy.capacity;
            this.lastRefillTime = Instant.now();
            this.lastAccessTime = Instant.now();
        }

        public synchronized boolean tryConsume() {
            Instant now = Instant.now();
            double secondsElapsed = (now.toEpochMilli() - lastRefillTime.toEpochMilli()) / 1000.0;

            if (secondsElapsed > 0) {
                tokens = Math.min(capacity, tokens + secondsElapsed * refillPerSecond);
                lastRefillTime = now;
            }

            lastAccessTime = now;

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        public synchronized Instant getLastAccessTime() {
            return lastAccessTime;
        }
    }

    // key = "IP:POLICY_NAME"
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------
    // Xử lý request
    // ----------------------------------------------------------------
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        RateLimitPolicy policy = resolvePolicy(path);

        if (policy != null) {
            String clientIp = getClientIp(request);
            String bucketKey = clientIp + ":" + policy.name();
            TokenBucket bucket = buckets.computeIfAbsent(bucketKey, k -> new TokenBucket(policy));

            if (!bucket.tryConsume()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(buildErrorMessage(policy));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Xác định policy phù hợp với đường dẫn request.
     * Trả về null nếu endpoint không cần giới hạn.
     */
    private RateLimitPolicy resolvePolicy(String path) {
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            return RateLimitPolicy.AUTH;
        }
        if (path.startsWith("/api/orders/checkout") || path.startsWith("/api/payments")) {
            return RateLimitPolicy.CHECKOUT;
        }
        if (path.startsWith("/api/ai")) {
            return RateLimitPolicy.AI;
        }
        return null;
    }

    private String buildErrorMessage(RateLimitPolicy policy) {
        return String.format(
            "{\"error\": \"Too many requests. Limit is %d requests per minute for this endpoint. Please wait before retrying.\"}",
            policy.capacity
        );
    }

    // ----------------------------------------------------------------
    // Cleanup định kỳ: xóa bucket không hoạt động > 5 phút
    // tránh memory leak khi có nhiều IP khác nhau
    // ----------------------------------------------------------------
    @Scheduled(fixedDelay = 5 * 60 * 1000) // chạy mỗi 5 phút
    public void cleanupExpiredBuckets() {
        Instant cutoff = Instant.now().minusSeconds(5 * 60);
        buckets.entrySet().removeIf(entry -> entry.getValue().getLastAccessTime().isBefore(cutoff));
    }

    // ----------------------------------------------------------------
    // Lấy IP thực của client (hỗ trợ reverse proxy / load balancer)
    // ----------------------------------------------------------------
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
