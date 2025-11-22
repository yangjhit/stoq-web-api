package com.stoq.repository;
import com.stoq.entity.CompanyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {
    
    // 根据公司ID和用户邮箱查找成员
    Optional<CompanyMember> findByCompanyIdAndUserEmail(Long companyId, String userEmail);
    
    // 根据公司ID查找所有成员
    List<CompanyMember> findByCompanyId(Long companyId);
    
    // 根据用户邮箱查找所有成员
    List<CompanyMember> findByUserEmail(String userEmail);
    
    // 根据公司ID和角色查找成员
    List<CompanyMember> findByCompanyIdAndRole(Long companyId, String role);
    
    // 根据公司ID删除所有成员
    void deleteByCompanyId(Long companyId);
}
