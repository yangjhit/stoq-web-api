package com.stoq.config;

import com.stoq.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // 验证token
                if (jwtUtil.validateToken(token)) {
                    // 从token中获取用户邮箱
                    String email = jwtUtil.getEmailFromToken(token);
                    
                    if (email != null) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                        
                        // 设置到SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("User authenticated: {}", email);
                    }
                }
            } catch (Exception e) {
                log.error("JWT authentication failed: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
