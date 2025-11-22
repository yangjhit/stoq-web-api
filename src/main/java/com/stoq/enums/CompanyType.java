package com.stoq.enums;
/**
 * 公司类型枚举
 */
public enum CompanyType {
    
    PERSONAL("PERSONAL", "Personal company"),
    PROFESSIONAL("PROFESSIONAL", "Professional company");
    
    private final String code;
    private final String description;
    
    CompanyType(String code, String description) {
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
    public static CompanyType fromCode(String code) {
        for (CompanyType type : CompanyType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown company type: " + code);
    }
}
