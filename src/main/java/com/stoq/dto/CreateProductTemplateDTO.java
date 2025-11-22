package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductTemplateDTO {
    
    @NotBlank(message = "Template name cannot be empty")
    @Size(min = 2, max = 200, message = "Template name must be between 2-200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private List<String> images; // 图片URL列表
    
    @NotBlank(message = "Unit cannot be empty")
    @Size(min = 1, max = 100, message = "Unit must be between 1-100 characters")
    private String unit;
    
    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotBlank(message = "Currency cannot be empty")
    @Size(min = 1, max = 20, message = "Currency must be between 1-20 characters")
    private String currency;
    
    @NotBlank(message = "Supplier cannot be empty")
    @Size(min = 2, max = 200, message = "Supplier must be between 2-200 characters")
    private String supplier;
    
    @NotBlank(message = "Supplier country cannot be empty")
    @Size(min = 2, max = 100, message = "Supplier country must be between 2-100 characters")
    private String supplierCountry;
    
    private String barCode; // Optional
    
    private String qrCode; // Optional
    
    @NotNull(message = "Category ID cannot be empty")
    private Long categoryId;
}
