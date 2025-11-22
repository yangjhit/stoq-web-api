package com.stoq.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    
    ADMIN("ADMIN", "Administrator - Full access to company"),
    MEMBER("MEMBER", "Member - No permissions");
    
    private final String code;
    private final String description;
    
    UserRole(String code, String description) {
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
    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + code);
    }
}
