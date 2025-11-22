package com.stoq.repository;

import com.stoq.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    // 根据团队ID查找所有成员
    List<TeamMember> findByTeamId(Long teamId);
    
    // 根据ID和团队ID查找成员
    Optional<TeamMember> findByIdAndTeamId(Long id, Long teamId);
    
    // 根据邮箱查找成员
    Optional<TeamMember> findByEmail(String email);
    
    // 根据团队ID和邮箱查找成员
    Optional<TeamMember> findByTeamIdAndEmail(Long teamId, String email);
    
    // 根据创建者邮箱查找所有成员
    List<TeamMember> findByCreatorEmail(String creatorEmail);
    
    // 根据关联的用户邮箱查找成员
    List<TeamMember> findByLinkedUserEmail(String linkedUserEmail);
}
