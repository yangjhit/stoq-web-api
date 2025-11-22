package com.stoq.dto;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserRegistrationDTO {
    
    private String avatar; // 头像URL,可选
    
    @NotBlank(message = "名字不能为空")
    @Size(min = 1, max = 50, message = "名字长度必须在1-50个字符之间")
    private String name;
    
    @NotBlank(message = "姓氏不能为空")
    @Size(min = 1, max = 50, message = "姓氏长度必须在1-50个字符之间")
    private String surName;
    
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄必须小于150")
    private Integer age;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,20}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "国家不能为空")
    @Size(min = 2, max = 100, message = "国家名称长度必须在2-100个字符之间")
    private String country;
    
    @NotBlank(message = "城市不能为空")
    @Size(min = 2, max = 100, message = "城市名称长度必须在2-100个字符之间")
    private String city;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "密码必须包含大小写字母和数字"
    )
    private String password;
    
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verificationCode; // 邮箱验证码
}
