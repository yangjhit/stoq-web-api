package com.stoq.repository;
import com.stoq.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    // 根据集群ID查找所有分类
    List<ProductCategory> findByClusterId(Long clusterId);
    
    // 根据ID和集群ID查找分类
    Optional<ProductCategory> findByIdAndClusterId(Long id, Long clusterId);
    
    // 根据创建者邮箱查找所有分类
    List<ProductCategory> findByCreatorEmail(String creatorEmail);
    
    // 根据集群ID和分类名称查找
    Optional<ProductCategory> findByClusterIdAndName(Long clusterId, String name);
}
