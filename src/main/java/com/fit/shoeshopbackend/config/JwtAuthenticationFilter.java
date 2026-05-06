package com.fit.shoeshopbackend.config;


import com.fit.shoeshopbackend.service.AccountDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private AccountDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = parseJwt(request);
        System.out.println("[JWT Filter] Request URI: " + request.getRequestURI() + ", Token present: " + (token != null));
        if (token != null) {
            try {
                if (jwtUtil.validateJwt(token)) {
                    String username = jwtUtil.getUsernameFromJwt(token);
                    System.out.println("[JWT Filter] Username from JWT: " + username);
                    var userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("[JWT Filter] Authenticated " + username + " successfully with: " + userDetails.getAuthorities());
                } else {
                    System.out.println("[JWT Filter] Token validation FAILED");
                }
            } catch (Exception e) {
                System.err.println("[JWT Filter] Auth exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}










