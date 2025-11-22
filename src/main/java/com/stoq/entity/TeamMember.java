package com.stoq.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"teamId", "email"})
})
@Data
public class TeamMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long teamId; // 团队ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId", insertable = false, updatable = false)
    private Team team;
    
    @Column(length = 500)
    private String avatar; // 头像URL
    
    @Column(nullable = false, length = 100)
    private String name; // 名字
    
    @Column(nullable = false, length = 100)
    private String surname; // 姓氏
    
    @Column(length = 1000)
    private String description; // 描述
    
    @Column(nullable = false)
    private Long countryId; // 国家ID
    
    @Column(nullable = false, length = 100)
    private String city; // 城市
    
    @Column(nullable = false, length = 100)
    private String stoq; // 所属仓库
    
    @Column(nullable = false, length = 100, unique = true)
    private String email; // 邮箱(唯一标识)
    
    @Column(nullable = false, length = 20)
    private String phone; // 电话
    
    @Column(nullable = false, length = 20)
    private String role; // 角色: ADMIN, MEMBER
    
    @Column(length = 100)
    private String linkedUserEmail; // 关联的已注册用户邮箱(可选)
    
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
