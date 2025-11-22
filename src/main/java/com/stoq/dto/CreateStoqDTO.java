package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateStoqDTO {
    
    @NotBlank(message = "Stoq name cannot be empty")
    @Size(min = 2, max = 200, message = "Stoq name must be between 2-200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Administrator cannot be empty")
    @Size(min = 2, max = 100, message = "Administrator must be between 2-100 characters")
    private String administrator;
    
    @NotNull(message = "Company ID cannot be empty")
    private Long companyId;
}
