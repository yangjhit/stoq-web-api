package com.stoq.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamResponseDTO {
    
    private Long id;
    private String name;
    private String logo;
    private String description;
    private Long companyId;
    private String companyName;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
