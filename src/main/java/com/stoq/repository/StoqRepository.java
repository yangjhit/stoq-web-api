package com.stoq.repository;
import com.stoq.entity.Stoq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoqRepository extends JpaRepository<Stoq, Long> {
    
    // 根据集群ID查找所有仓库
    List<Stoq> findByClusterId(Long clusterId);
    
    // 根据ID和集群ID查找仓库
    Optional<Stoq> findByIdAndClusterId(Long id, Long clusterId);
    
    // 根据创建者邮箱查找所有仓库
    List<Stoq> findByCreatorEmail(String creatorEmail);
    
    // 根据管理员查找仓库
    List<Stoq> findByAdministrator(String administrator);
}
