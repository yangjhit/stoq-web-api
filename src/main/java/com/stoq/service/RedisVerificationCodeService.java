package com.stoq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisVerificationCodeService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String VERIFICATION_CODE_PREFIX = "verification_code:";
    private static final long EXPIRATION_MINUTES = 5; // 5分钟有效期
    
    /**
     * 保存验证码到Redis(支持场景)
     */
    public void saveVerificationCode(String email, String code, String scenario) {
        String key = VERIFICATION_CODE_PREFIX + scenario + ":" + email;
        redisTemplate.opsForValue().set(key, code, EXPIRATION_MINUTES, TimeUnit.MINUTES);
        log.info("✅ 验证码已保存到Redis: {} (场景: {}, 有效期: {}分钟)", email, scenario, EXPIRATION_MINUTES);
    }
    
    /**
     * 从Redis获取验证码(支持场景)
     */
    public String getVerificationCode(String email, String scenario) {
        String key = VERIFICATION_CODE_PREFIX + scenario + ":" + email;
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 验证验证码(支持场景)
     */
    public boolean verifyCode(String email, String code, String scenario) {
        String storedCode = getVerificationCode(email, scenario);
        if (storedCode == null) {
            log.warn("❌ 验证码不存在或已过期: {} (场景: {})", email, scenario);
            return false;
        }
        
        boolean isValid = storedCode.equals(code);
        if (isValid) {
            log.info("✅ 验证码验证成功: {} (场景: {})", email, scenario);
            // 验证成功后立即删除验证码
            deleteVerificationCode(email, scenario);
        } else {
            log.warn("❌ 验证码不正确: {} (场景: {})", email, scenario);
        }
        return isValid;
    }
    
    /**
     * 删除验证码(支持场景)
     */
    public void deleteVerificationCode(String email, String scenario) {
        String key = VERIFICATION_CODE_PREFIX + scenario + ":" + email;
        redisTemplate.delete(key);
        log.info("✅ 验证码已删除: {} (场景: {})", email, scenario);
    }
    
    /**
     * 检查验证码是否存在(支持场景)
     */
    public boolean exists(String email, String scenario) {
        String key = VERIFICATION_CODE_PREFIX + scenario + ":" + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
