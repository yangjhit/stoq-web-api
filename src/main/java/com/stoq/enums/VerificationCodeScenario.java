package com.stoq.enums;
/**
 * 验证码使用场景枚举
 */
public enum VerificationCodeScenario {
    
    REGISTER("REGISTER", "用户注册"),
    RESET_PASSWORD("RESET_PASSWORD", "重置密码");
    
    private final String code;
    private final String description;
    
    VerificationCodeScenario(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static VerificationCodeScenario fromCode(String code) {
        for (VerificationCodeScenario scenario : VerificationCodeScenario.values()) {
            if (scenario.code.equals(code)) {
                return scenario;
            }
        }
        throw new IllegalArgumentException("未知的验证码场景: " + code);
    }
}
