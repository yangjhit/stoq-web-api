package com.stoq.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductCategoryResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private String image;
    private Long companyId;
    private String companyName;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
