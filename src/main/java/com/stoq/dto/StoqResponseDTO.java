package com.stoq.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoqResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private String administrator;
    private Long clusterId;
    private String clusterName;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
