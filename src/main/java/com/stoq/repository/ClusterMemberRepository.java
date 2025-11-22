package com.stoq.repository;
import com.stoq.entity.ClusterMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClusterMemberRepository extends JpaRepository<ClusterMember, Long> {
    
    // 根据集群ID和用户邮箱查找成员
    Optional<ClusterMember> findByClusterIdAndUserEmail(Long clusterId, String userEmail);
    
    // 根据集群ID查找所有成员
    List<ClusterMember> findByClusterId(Long clusterId);
    
    // 根据用户邮箱查找所有成员
    List<ClusterMember> findByUserEmail(String userEmail);
    
    // 根据集群ID和角色查找成员
    List<ClusterMember> findByClusterIdAndRole(Long clusterId, String role);
    
    // 根据集群ID删除所有成员
    void deleteByClusterId(Long clusterId);
}
