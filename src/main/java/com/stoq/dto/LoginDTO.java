package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is incorrect")
    private String email;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
