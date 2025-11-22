package com.stoq.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SendVerificationCodeDTO {
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is incorrect")
    private String email;
    
    @NotBlank(message = "Scenario cannot be empty")
    private String scenario; // REGISTER / RESET_PASSWORD
}
