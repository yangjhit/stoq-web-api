package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordDTO {
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is incorrect")
    private String email;
    
    @NotBlank(message = "New password cannot be empty")
    @Size(min = 6, max = 50, message = "Password length must be between 6-50 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "Password must contain uppercase, lowercase letters and numbers"
    )
    private String newPassword;
    
    @NotBlank(message = "Confirm password cannot be empty")
    private String confirmPassword;
    
    @NotBlank(message = "Verification code cannot be empty")
    @Pattern(regexp = "^\\d{6}$", message = "Verification code must be 6 digits")
    private String verificationCode;
}
