package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RefreshTokenDTO {
    
    @NotBlank(message = "Token cannot be empty")
    private String token;
}
