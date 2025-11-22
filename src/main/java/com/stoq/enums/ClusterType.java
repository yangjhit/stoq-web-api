package com.stoq.enums;
/**
 * 集群类型枚举
 */
public enum ClusterType {
    
    PERSONAL("PERSONAL", "Personal cluster"),
    PROFESSIONAL("PROFESSIONAL", "Professional cluster");
    
    private final String code;
    private final String description;
    
    ClusterType(String code, String description) {
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
    public static ClusterType fromCode(String code) {
        for (ClusterType type : ClusterType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown cluster type: " + code);
    }
}
