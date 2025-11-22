package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stoqs")
@Data
public class Stoq {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name; // 仓库名称
    
    @Column(length = 1000)
    private String description; // 仓库描述
    
    @Column(nullable = false, length = 100)
    private String administrator; // 管理员邮箱或名称
    
    @Column(nullable = false)
    private Long companyId; // 所属公司ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", insertable = false, updatable = false)
    private Company company; // 所属公司
    
    @Column(nullable = false, length = 100)
    private String creatorEmail; // 创建者邮箱
    
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
