package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Data
public class VerificationCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 6)
    private String code; // 6位验证码
    
    @Column(nullable = false, length = 20)
    private String scenario = "REGISTER"; // 验证码场景: REGISTER(注册) / RESET_PASSWORD(重置密码)
    
    @Column(nullable = false)
    private Boolean verified = false; // 是否已验证
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // 过期时间(5分钟后)
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusMinutes(5); // 5分钟有效期
    }
}
