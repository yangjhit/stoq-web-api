package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_categories")
@Data
public class ProductCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "clusterId", nullable = false)
    private Long clusterId; // 集群ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clusterId", insertable = false, updatable = false)
    private Cluster cluster;
    
    @Column(nullable = false, length = 200)
    private String name; // 分类名称
    
    @Column(length = 1000)
    private String description; // 分类描述
    
    @Column(length = 500)
    private String image; // 分类图片URL
    
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
