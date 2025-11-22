package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @Column(unique = true, nullable = false, length = 100)
    private String email; // 使用email作为主键
    
    @Column(length = 500)
    private String avatar; // 头像URL
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "sur_name", nullable = false, length = 50)
    private String surName;
    
    @Column(nullable = false)
    private Integer age;
    
    @Column(nullable = false, length = 20)
    private String phone;
    
    @Column(nullable = false, length = 100)
    private String country;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 255)
    private String password; // 实际应用中应该加密存储
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
