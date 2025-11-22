package com.stoq.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoqResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private String administrator;
    private Long companyId;
    private String companyName; // 公司名称
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
