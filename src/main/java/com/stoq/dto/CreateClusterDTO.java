package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateClusterDTO {
    
    @NotBlank(message = "Cluster name cannot be empty")
    @Size(min = 2, max = 200, message = "Cluster name must be between 2-200 characters")
    private String name;
    
    private String logo; // Optional
    
    @NotBlank(message = "Address cannot be empty")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
    
    @NotNull(message = "Country ID cannot be empty")
    private Long countryId;
    
    @NotBlank(message = "City cannot be empty")
    @Size(min = 2, max = 100, message = "City must be between 2-100 characters")
    private String city;
    
    @NotBlank(message = "Field cannot be empty")
    @Size(min = 2, max = 100, message = "Field must be between 2-100 characters")
    private String field;
    
    @NotNull(message = "Employee count cannot be empty")
    @Min(value = 1, message = "Employee count must be at least 1")
    private Integer employeeCount;
    
    @NotBlank(message = "Cluster type cannot be empty")
    @Pattern(regexp = "^(PERSONAL|PROFESSIONAL)$", message = "Type must be PERSONAL or PROFESSIONAL")
    private String type;
    
    // Registration number is required only for PROFESSIONAL type
    private String registrationNumber;
}
