package com.stoq.repository;
import com.stoq.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    // 根据公司ID查找所有团队
    List<Team> findByCompanyId(Long companyId);
    
    // 根据ID和公司ID查找团队
    Optional<Team> findByIdAndCompanyId(Long id, Long companyId);
    
    // 根据创建者邮箱查找所有团队
    List<Team> findByCreatorEmail(String creatorEmail);
}
