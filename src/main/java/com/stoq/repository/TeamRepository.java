package com.stoq.repository;
import com.stoq.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // 根据集群ID查找所有团队
    List<Team> findByClusterId(Long clusterId);
    
    // 根据ID和集群ID查找团队
    Optional<Team> findByIdAndClusterId(Long id, Long clusterId);
    
    // 根据创建者邮箱查找所有团队
    List<Team> findByCreatorEmail(String creatorEmail);
}
