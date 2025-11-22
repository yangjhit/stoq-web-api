package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"companyId", "userEmail"})
})
@Data
public class CompanyMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long companyId; // 公司ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", insertable = false, updatable = false)
    private Company company;
    
    @Column(nullable = false, length = 100)
    private String userEmail; // 用户邮箱
    
    @Column(nullable = false, length = 20)
    private String role; // 角色: ADMIN, MANAGER, MEMBER
    
    @Column(nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
