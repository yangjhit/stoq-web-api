package com.stoq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    // 验证码存储(用于测试和备份)
    private static final Map<String, String> verificationCodeStorage = new HashMap<>();
    
    /**
     * 发送验证码邮件
     */
    public void sendVerificationCode(String email, String code) {
        // 总是存储验证码到内存(用于测试)
        verificationCodeStorage.put(email, code);
        
        // 尝试发送真实邮件
        if (mailSender != null) {
            try {
                sendRealEmail(email, code);
            } catch (Exception e) {
                log.warn("⚠️ 真实邮件发送失败,但验证码已保存。错误: {}", e.getMessage());
                log.info("💡 开发提示: 验证码已存储在内存中,可以通过 /api/users/get-verification-code 接口获取");
            }
        } else {
            log.info("📧 [模拟模式] 验证码已发送到: {} (验证码: {})", email, code);
            log.info("💡 提示: 邮件服务未配置,验证码已存储在内存中。可以通过 /api/users/get-verification-code 接口获取");
        }
    }
    
    /**
     * 真实发送邮件
     */
    private void sendRealEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@stoq.com");
            message.setTo(email);
            message.setSubject("Stoq 用户注册验证码");
            message.setText(buildVerificationCodeEmailBody(code));
            
            mailSender.send(message);
            log.info("✅ 验证码邮件已发送到: {}", email);
        } catch (Exception e) {
            log.error("❌ 发送验证码邮件失败: {}", email, e);
            throw e;
        }
    }
    
    /**
     * 获取验证码(仅用于开发测试)
     */
    public String getVerificationCode(String email) {
        return verificationCodeStorage.get(email);
    }
    
    /**
     * 构建验证码邮件内容
     */
    private String buildVerificationCodeEmailBody(String code) {
        return "尊敬的用户,\n\n" +
                "感谢您注册Stoq账户。\n\n" +
                "您的验证码是: " + code + "\n\n" +
                "此验证码有效期为10分钟,请勿泄露给他人。\n\n" +
                "如果您没有进行此操作,请忽略此邮件。\n\n" +
                "Stoq团队";
    }
}
