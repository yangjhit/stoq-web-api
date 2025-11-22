package com.stoq.service;
import com.stoq.dto.LoginDTO;
import com.stoq.dto.LoginResponseDTO;
import com.stoq.dto.ResetPasswordDTO;
import com.stoq.dto.UserRegistrationDTO;
import com.stoq.dto.UserResponseDTO;
import com.stoq.entity.User;
import com.stoq.exception.EmailAlreadyExistsException;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.UserRepository;
import com.stoq.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册 (验证码在注册时提供)
     */
    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO dto) {
        // 1. 验证邮箱验证码(注册场景)
        verificationCodeService.verifyCode(dto.getEmail(), dto.getVerificationCode(), "REGISTER");
        
        // 2. 检查邮箱是否已存在
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("邮箱已被注册: " + dto.getEmail());
        }
        
        // 3. 创建用户实体
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setName(dto.getName());
        user.setSurName(dto.getSurName());
        user.setAge(dto.getAge());
        user.setPhone(dto.getPhone());
        user.setCountry(dto.getCountry());
        user.setCity(dto.getCity());
        
        // 4. 密码加密 (使用BCrypt)
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // 5. 保存用户
        User savedUser = userRepository.save(user);
        
        // 6. 转换为响应DTO (不包含密码)
        return convertToResponseDTO(savedUser);
    }
    
    /**
     * 根据邮箱获取用户信息
     */
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + email));
        return convertToResponseDTO(user);
    }
    
    /**
     * 获取所有用户
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public UserResponseDTO updateUser(String email, UserRegistrationDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + email));
        
        // 如果要修改邮箱,需要检查新邮箱是否已存在
        if (!email.equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("邮箱已被注册: " + dto.getEmail());
        }
        
        // 更新用户信息
        user.setAvatar(dto.getAvatar());
        user.setName(dto.getName());
        user.setSurName(dto.getSurName());
        user.setAge(dto.getAge());
        user.setPhone(dto.getPhone());
        user.setCountry(dto.getCountry());
        user.setCity(dto.getCity());
        
        User updatedUser = userRepository.save(user);
        return convertToResponseDTO(updatedUser);
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + email));
        userRepository.delete(user);
    }
    
    /**
     * 用户登录
     */
    public LoginResponseDTO login(LoginDTO dto) {
        // 1. 查找用户
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getEmail()));
        
        // 2. 验证密码 (使用BCrypt比较)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        
        // 3. 生成JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        
        // 4. 返回登录响应
        return new LoginResponseDTO(
                token,
                user.getEmail(),
                user.getName(),
                user.getSurName(),
                user.getAge(),
                user.getPhone(),
                user.getCountry(),
                user.getCity(),
                user.getAvatar(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    
    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        // 1. 验证两次密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        
        // 2. 查找用户(先检查用户是否存在)
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + dto.getEmail()));
        
        // 3. 验证邮箱验证码(重置密码场景)
        verificationCodeService.verifyCode(dto.getEmail(), dto.getVerificationCode(), "RESET_PASSWORD");
        
        // 4. 更新密码 (使用BCrypt加密)
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        
        // 5. 清理该邮箱的所有验证码
        verificationCodeService.deleteVerificationCodes(dto.getEmail());
    }
    
    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        return jwtUtil.refreshToken(token);
    }
    
    /**
     * 转换为响应DTO (不包含密码)
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setName(user.getName());
        dto.setSurName(user.getSurName());
        dto.setAge(user.getAge());
        dto.setPhone(user.getPhone());
        dto.setCountry(user.getCountry());
        dto.setCity(user.getCity());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
