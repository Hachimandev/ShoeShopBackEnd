package com.fit.shoeshopbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Simple Token Bucket per IP address
    private static class TokenBucket {
        private final long capacity = 30; // Max 30 requests
        private final long refillRatePerSecond = 1; // Refill 1 token per second (fully refilled in 30s)
        private double tokens = 30;
        private Instant lastRefillTime = Instant.now();

        public synchronized boolean tryConsume() {
            Instant now = Instant.now();
            long secondsElapsed = now.getEpochSecond() - lastRefillTime.getEpochSecond();
            if (secondsElapsed > 0) {
                tokens = Math.min(capacity, tokens + secondsElapsed * refillRatePerSecond);
                lastRefillTime = now;
            }
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }

    private final Map<String, TokenBucket> ipBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Apply rate limit to critical endpoints: Checkout and AI Chat
        if (path.startsWith("/api/orders/checkout") || path.startsWith("/api/ai")) {
            String clientIp = getClientIp(request);
            TokenBucket bucket = ipBuckets.computeIfAbsent(clientIp, k -> new TokenBucket());

            if (!bucket.tryConsume()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Too many requests. Limit is 30 requests per minute. Please wait.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
