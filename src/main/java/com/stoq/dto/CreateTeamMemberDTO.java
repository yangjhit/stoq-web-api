package com.stoq.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateTeamMemberDTO {
    
    private String avatar; // Optional
    
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 100, message = "Name must be between 1-100 characters")
    private String name;
    
    @NotBlank(message = "Surname cannot be empty")
    @Size(min = 1, max = 100, message = "Surname must be between 1-100 characters")
    private String surname;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Country ID cannot be empty")
    private Long countryId;
    
    @NotBlank(message = "City cannot be empty")
    @Size(min = 2, max = 100, message = "City must be between 2-100 characters")
    private String city;
    
    @NotBlank(message = "Stoq cannot be empty")
    @Size(min = 2, max = 100, message = "Stoq must be between 2-100 characters")
    private String stoq;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is incorrect")
    private String email; // 团队成员邮箱(唯一标识)
    
    @NotBlank(message = "Phone cannot be empty")
    @Size(min = 8, max = 20, message = "Phone must be between 8-20 characters")
    private String phone;
    
    @NotBlank(message = "Role cannot be empty")
    @Pattern(regexp = "^(ADMIN|MEMBER)$", message = "Role must be ADMIN or MEMBER")
    private String role; // ADMIN or MEMBER
    
    @NotNull(message = "Team ID cannot be empty")
    private Long teamId;
}
