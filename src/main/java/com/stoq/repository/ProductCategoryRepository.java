package com.stoq.repository;
import com.stoq.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    // 根据公司ID查找所有分类
    List<ProductCategory> findByCompanyId(Long companyId);
    
    // 根据ID和公司ID查找分类
    Optional<ProductCategory> findByIdAndCompanyId(Long id, Long companyId);
    
    // 根据创建者邮箱查找所有分类
    List<ProductCategory> findByCreatorEmail(String creatorEmail);
    
    // 根据公司ID和分类名称查找
    Optional<ProductCategory> findByCompanyIdAndName(Long companyId, String name);
}
