package com.stoq.repository;
import com.stoq.entity.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Long> {
    
    // 根据分类ID查找所有模板
    List<ProductTemplate> findByCategoryId(Long categoryId);
    
    // 根据ID和分类ID查找模板
    Optional<ProductTemplate> findByIdAndCategoryId(Long id, Long categoryId);
    
    // 根据创建者邮箱查找所有模板
    List<ProductTemplate> findByCreatorEmail(String creatorEmail);
    
    // 根据分类ID和模板名称查找
    Optional<ProductTemplate> findByCategoryIdAndName(Long categoryId, String name);
}
