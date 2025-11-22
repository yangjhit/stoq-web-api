package com.stoq.controller;

import com.stoq.dto.*;
import com.stoq.service.EmailService;
import com.stoq.service.UserService;
import com.stoq.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户注册、查询、更新、删除相关API")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VerificationCodeService verificationCodeService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Send verification code to email
     */
    @PostMapping("/send-verification-code")
    @Operation(summary = "Send verification code", description = "Send a 6-digit verification code to the specified email")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@Validated @RequestBody SendVerificationCodeDTO dto) {
        verificationCodeService.generateAndSendCode(dto.getEmail(), dto.getScenario());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification code sent to: " + dto.getEmail());
        response.put("email", dto.getEmail());
        response.put("scenario", dto.getScenario());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get verification code (for development testing only)
     */
    @GetMapping("/get-verification-code")
    @Operation(summary = "Get verification code", description = "Get the sent verification code, for development testing only")
    public ResponseEntity<Map<String, String>> getVerificationCode(@RequestParam String email, @RequestParam String scenario) {
        String code = emailService.getVerificationCode(email);
        Map<String, String> response = new HashMap<>();
        if (code != null) {
            response.put("email", email);
            response.put("scenario", scenario);
            response.put("verificationCode", code);
            response.put("message", "Verification code retrieved successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("email", email);
            response.put("scenario", scenario);
            response.put("message", "Verification code not found, please send it first");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * User registration
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Create a new user account with email as unique identifier")
    public ResponseEntity<UserResponseDTO> registerUser(@Validated @RequestBody UserRegistrationDTO dto) {
        UserResponseDTO user = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    /**
     * User login
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with email and password, return user info and JWT token")
    public ResponseEntity<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        com.stoq.dto.LoginResponseDTO loginResponse = userService.login(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("token", loginResponse.getToken());
        response.put("user", loginResponse);
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset password
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password with email and verification code")
    public ResponseEntity<Map<String, String>> resetPassword(@Validated @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully, please login with new password");
        response.put("email", dto.getEmail());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token", description = "Get a new token with extended validity period of 24 hours")
    public ResponseEntity<Map<String, String>> refreshToken(@Validated @RequestBody RefreshTokenDTO dto) {
        String newToken = userService.refreshToken(dto.getToken());
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);
        response.put("message", "Token refreshed successfully, new token valid for 24 hours");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user information
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current logged-in user information", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Get all users or query a single user by email (requires authentication)
     */
    @GetMapping
    @Operation(summary = "Get users", description = "Get all users if no email parameter, or get a specific user if email parameter is provided (requires authentication)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String email) {
        if (email != null && !email.isEmpty()) {
            // Query a single user
            UserResponseDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } else {
            // Get all users
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
    }
    
    /**
     * Update current user information
     */
    @PutMapping("/me")
    @Operation(summary = "Update current user", description = "Update current logged-in user information",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@Validated @RequestBody UserRegistrationDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponseDTO user = userService.updateUser(email, dto);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Update user information (admin only)
     */
    @PutMapping
    @Operation(summary = "Update user information", description = "Update user personal information by email (requires authentication)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponseDTO> updateUser(
            @RequestParam String email,
            @Validated @RequestBody UserRegistrationDTO dto) {
        UserResponseDTO user = userService.updateUser(email, dto);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Delete current user account
     */
    @DeleteMapping("/me")
    @Operation(summary = "Delete current user", description = "Delete current logged-in user account",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Delete user (admin only)
     */
    @DeleteMapping
    @Operation(summary = "Delete user", description = "Delete user account by email (requires authentication)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteUser(@RequestParam String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}
