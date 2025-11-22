package com.stoq.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret:stoq-web-api-secret-key-2024-very-long-secret-key-for-jwt-token-generation-must-be-at-least-512-bits-long-for-hs512-algorithm}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 默认24小时
    private long expiration;
    
    /**
     * 生成JWT token
     */
    public String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 从token中获取邮箱
     */
    public String getEmailFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("获取token中的邮箱失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 刷新Token(生成新的Token)
     */
    public String refreshToken(String token) {
        // 1. 验证旧token是否有效
        if (!validateToken(token)) {
            throw new RuntimeException("Token无效或已过期,无法刷新");
        }
        
        // 2. 从旧token中获取邮箱
        String email = getEmailFromToken(token);
        if (email == null) {
            throw new RuntimeException("无法从Token中获取用户信息");
        }
        
        // 3. 生成新的token
        String newToken = generateToken(email);
        log.info("✅ Token已刷新: {}", email);
        return newToken;
    }
}
