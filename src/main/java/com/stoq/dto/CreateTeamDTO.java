package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateTeamDTO {
    
    @NotBlank(message = "Team name cannot be empty")
    @Size(min = 2, max = 200, message = "Team name must be between 2-200 characters")
    private String name;
    
    private String logo; // Optional
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Cluster ID cannot be empty")
    private Long clusterId;
}
