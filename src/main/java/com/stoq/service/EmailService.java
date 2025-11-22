package com.stoq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${stoq.mail.from-address:${spring.mail.username:}}")
    private String fromAddress;

    @Autowired
    private MessageSource messageSource;

    // 验证码存储(用于测试和备份)
    private static final Map<String, String> verificationCodeStorage = new HashMap<>();
    
    /**
     * 发送验证码邮件(支持国际化)
     */
    public void sendVerificationCode(String email, String code, Locale locale) {
        // 总是存储验证码到内存(用于测试)
        verificationCodeStorage.put(email, code);
        
        // 尝试发送真实邮件
        if (mailSender != null) {
            try {
                sendRealEmail(email, code, locale);
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
     * 兼容旧调用,默认使用系统Locale
     */
    public void sendVerificationCode(String email, String code) {
        sendVerificationCode(email, code, LocaleContextHolder.getLocale());
    }

    /**
     * 真实发送邮件
     */
    private void sendRealEmail(String email, String code, Locale locale) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromAddress != null && !fromAddress.isBlank()) {
                message.setFrom(fromAddress);
            }
            message.setTo(email);
            message.setSubject(messageSource.getMessage("email.subject.verification", null, locale));
            message.setText(messageSource.getMessage("email.body.verification", new Object[]{code}, locale));

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
