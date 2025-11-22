package com.stoq.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private String email;
    private String avatar;
    private String name;
    private String surName;
    private Integer age;
    private String phone;
    private String country;
    private String city;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 注意: 不包含password字段,保护用户隐私
}
