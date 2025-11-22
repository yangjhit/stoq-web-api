package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Data
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name; // 公司名称
    
    @Column(length = 500)
    private String logo; // 公司Logo URL
    
    @Column(nullable = false, length = 500)
    private String address; // 公司地址
    
    @Column(nullable = false)
    private Long countryId; // 国家ID
    
    @Column(nullable = false, length = 100)
    private String city; // 城市
    
    @Column(nullable = false, length = 100)
    private String field; // 公司领域/行业
    
    @Column(nullable = false)
    private Integer employeeCount; // 员工数
    
    @Column(nullable = false, length = 20)
    private String type; // 类型: PERSONAL 或 PROFESSIONAL
    
    @Column(length = 100)
    private String registrationNumber; // 注册号(仅PROFESSIONAL类型需要)
    
    @Column(nullable = false, length = 100)
    private String ownerEmail; // 创建者邮箱
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
