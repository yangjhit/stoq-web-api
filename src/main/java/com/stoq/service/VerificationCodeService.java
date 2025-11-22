package com.stoq.service;

import com.stoq.entity.VerificationCode;
import com.stoq.enums.VerificationCodeScenario;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {
    
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final RedisVerificationCodeService redisVerificationCodeService;
    private final Random random = new Random();
    private final MessageSource messageSource;

    /**
     * 生成并发送验证码(支持场景)
     */
    @Transactional
    public void generateAndSendCode(String email, String scenario, Locale locale) {
        // 1. 生成6位随机验证码
        String code = generateCode();
        
        // 2. 保存到Redis (优先使用Redis)
        try {
            redisVerificationCodeService.saveVerificationCode(email, code, scenario);
        } catch (Exception e) {
            log.warn("⚠️ Redis保存验证码失败,使用数据库备份: {}", e.getMessage());
            // 备份方案: 保存到数据库
            verificationCodeRepository.deleteByEmailAndScenario(email, scenario);
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
            verificationCode.setCode(code);
            verificationCode.setScenario(scenario);
            verificationCodeRepository.save(verificationCode);
        }
        
        // 3. 发送验证码邮件
        emailService.sendVerificationCode(email, code, locale);
    }
    
    /**
     * 验证验证码(支持场景)
     */
    @Transactional
    public boolean verifyCode(String email, String code, String scenario) {
        // 1. 优先从Redis验证
        try {
            if (redisVerificationCodeService.verifyCode(email, code, scenario)) {
                log.info("✅ 验证码已从Redis验证: {} (场景: {})", email, scenario);
                return true;
            }
        } catch (Exception e) {
            log.warn("⚠️ Redis验证失败,使用数据库备份: {}", e.getMessage());
        }
        
        // 2. 备份方案: 从数据库验证
        VerificationCode verificationCode = verificationCodeRepository
                .findByEmailAndCodeAndScenario(email, code, scenario)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("error.verification.invalid", null, LocaleContextHolder.getLocale())));

        // 检查是否过期
        if (LocalDateTime.now().isAfter(verificationCode.getExpiresAt())) {
            verificationCodeRepository.delete(verificationCode);
            throw new ResourceNotFoundException(messageSource.getMessage("error.verification.expired", null, LocaleContextHolder.getLocale()));
        }
        
        // 验证成功后立即删除验证码
        verificationCodeRepository.delete(verificationCode);
        log.info("✅ 验证码已从数据库验证并删除: {} (场景: {})", email, scenario);
        
        return true;
    }
    
    /**
     * 检查验证码是否已验证(支持场景)
     */
    public boolean isCodeVerified(String email, String scenario) {
        return verificationCodeRepository
                .findFirstByEmailAndScenarioOrderByCreatedAtDesc(email, scenario)
                .map(VerificationCode::getVerified)
                .orElse(false);
    }
    
    /**
     * 删除指定邮箱的所有验证码
     */
    @Transactional
    public void deleteVerificationCodes(String email) {
        verificationCodeRepository.deleteByEmail(email);
    }
    
    /**
     * 生成6位随机验证码
     */
    private String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
