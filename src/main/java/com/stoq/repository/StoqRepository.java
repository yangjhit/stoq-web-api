package com.stoq.repository;
import com.stoq.entity.Stoq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoqRepository extends JpaRepository<Stoq, Long> {
    
    // 根据公司ID查找所有仓库
    List<Stoq> findByCompanyId(Long companyId);
    
    // 根据ID和公司ID查找仓库
    Optional<Stoq> findByIdAndCompanyId(Long id, Long companyId);
    
    // 根据创建者邮箱查找所有仓库
    List<Stoq> findByCreatorEmail(String creatorEmail);
    
    // 根据管理员查找仓库
    List<Stoq> findByAdministrator(String administrator);
}
