package com.stoq.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private String email;
    private String name;
    private String surName;
    private Integer age;
    private String phone;
    private String country;
    private String city;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
