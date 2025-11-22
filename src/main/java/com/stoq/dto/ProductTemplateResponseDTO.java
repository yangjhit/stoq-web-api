package com.stoq.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductTemplateResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private List<String> images;
    private String unit;
    private BigDecimal price;
    private String currency;
    private String supplier;
    private String supplierCountry;
    private String barCode;
    private String qrCode;
    private Long categoryId;
    private String categoryName;
    private Long clusterId;
    private String clusterName;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
