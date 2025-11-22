package com.stoq.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClusterResponseDTO {
    
    private Long id;
    private String name;
    private String logo;
    private String address;
    private Long countryId;
    private String city;
    private String field;
    private Integer employeeCount;
    private String type;
    private String registrationNumber;
    private String ownerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
