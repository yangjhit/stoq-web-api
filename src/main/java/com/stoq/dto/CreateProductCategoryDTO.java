package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateProductCategoryDTO {
    
    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 2, max = 200, message = "Category name must be between 2-200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private String image; // Optional
    
    @NotNull(message = "Cluster ID cannot be empty")
    private Long clusterId;
}
