package com.stoq.entity;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_templates")
@Data
public class ProductTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long categoryId; // 商品分类ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private ProductCategory category;
    
    @Column(nullable = false, length = 200)
    private String name; // 模板名称
    
    @Column(length = 1000)
    private String description; // 模板描述
    
    @Column(columnDefinition = "LONGTEXT")
    private String images; // 图片URL列表(JSON格式,逗号分隔)
    
    @Column(nullable = false, length = 100)
    private String unit; // 单位(件、盒、箱等)
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 价格
    
    @Column(nullable = false, length = 20)
    private String currency; // 货币单位(CNY、USD等)
    
    @Column(nullable = false, length = 200)
    private String supplier; // 供应商
    
    @Column(nullable = false, length = 100)
    private String supplierCountry; // 供应商所在国家
    
    @Column(length = 100)
    private String barCode; // 条形码
    
    @Column(length = 100)
    private String qrCode; // 二维码
    
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
