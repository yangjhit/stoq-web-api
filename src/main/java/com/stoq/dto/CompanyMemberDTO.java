package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class CompanyMemberDTO {
    
    private Long id;
    
    @NotNull(message = "Company ID cannot be empty")
    private Long companyId;
    
    @NotBlank(message = "User email cannot be empty")
    @Email(message = "Email format is incorrect")
    private String userEmail;
    
    @NotBlank(message = "Role cannot be empty")
    @Pattern(regexp = "^(ADMIN|MANAGER|MEMBER)$", message = "Role must be ADMIN, MANAGER, or MEMBER")
    private String role;
    
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;
}
