package com.stoq.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamMemberResponseDTO {
    
    private Long id;
    private String avatar;
    private String name;
    private String surname;
    private String description;
    private Long countryId;
    private String city;
    private String stoq;
    private String email;
    private String phone;
    private String role;
    private String linkedUserEmail;
    private Long teamId;
    private String teamName;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
