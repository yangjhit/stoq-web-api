package com.stoq.repository;
import com.stoq.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    // 根据创建者邮箱查找公司列表
    List<Company> findByOwnerEmail(String ownerEmail);
    
    // 根据ID和创建者邮箱查找公司
    Optional<Company> findByIdAndOwnerEmail(Long id, String ownerEmail);
    
    // 根据公司名称查找
    Optional<Company> findByName(String name);
    
    // 根据类型查找
    List<Company> findByType(String type);
}
