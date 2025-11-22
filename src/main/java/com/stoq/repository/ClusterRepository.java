package com.stoq.repository;
import com.stoq.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    
    // 根据创建者邮箱查找集群列表
    List<Cluster> findByOwnerEmail(String ownerEmail);
    
    // 根据ID和创建者邮箱查找集群
    Optional<Cluster> findByIdAndOwnerEmail(Long id, String ownerEmail);
    
    // 根据集群名称查找
    Optional<Cluster> findByName(String name);
    
    // 根据类型查找
    List<Cluster> findByType(String type);
}
