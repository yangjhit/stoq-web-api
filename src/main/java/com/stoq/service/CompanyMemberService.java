package com.stoq.service;
import com.stoq.dto.CompanyMemberDTO;
import com.stoq.entity.Company;
import com.stoq.entity.CompanyMember;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.CompanyMemberRepository;
import com.stoq.repository.CompanyRepository;
import com.stoq.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyMemberService {
    
    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyRepository companyRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 添加公司成员(需要ADMIN权限)
     */
    @Transactional
    public CompanyMemberDTO addMember(CompanyMemberDTO dto, String operatorEmail) {
        // 验证公司是否存在
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + dto.getCompanyId()));
        
        // 验证操作者是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(dto.getCompanyId(), operatorEmail);
        
        // 检查成员是否已存在
        if (companyMemberRepository.findByCompanyIdAndUserEmail(dto.getCompanyId(), dto.getUserEmail()).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this company");
        }
        
        // 创建成员
        CompanyMember member = new CompanyMember();
        member.setCompanyId(dto.getCompanyId());
        member.setUserEmail(dto.getUserEmail());
        member.setRole(dto.getRole());
        
        CompanyMember savedMember = companyMemberRepository.save(member);
        return toDTO(savedMember);
    }
    
    /**
     * 获取公司的所有成员
     */
    public List<CompanyMemberDTO> getCompanyMembers(Long companyId, String userEmail) {
        // 验证公司是否存在
        companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        
        // 验证用户是否是公司成员
        permissionUtil.verifyCompanyMember(companyId, userEmail);
        
        List<CompanyMember> members = companyMemberRepository.findByCompanyId(companyId);
        return members.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新成员角色(需要ADMIN权限)
     */
    @Transactional
    public CompanyMemberDTO updateMemberRole(Long memberId, String newRole, String operatorEmail) {
        CompanyMember member = companyMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        
        // 验证操作者是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(member.getCompanyId(), operatorEmail);
        
        // 不允许修改最后一个ADMIN
        if ("ADMIN".equals(member.getRole()) && !"ADMIN".equals(newRole)) {
            long adminCount = companyMemberRepository.findByCompanyIdAndRole(member.getCompanyId(), "ADMIN").size();
            if (adminCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the last admin from the company");
            }
        }
        
        member.setRole(newRole);
        CompanyMember updatedMember = companyMemberRepository.save(member);
        return toDTO(updatedMember);
    }
    
    /**
     * 删除公司成员(需要ADMIN权限)
     */
    @Transactional
    public void removeMember(Long memberId, String operatorEmail) {
        CompanyMember member = companyMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        
        // 验证操作者是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(member.getCompanyId(), operatorEmail);
        
        // 不允许删除最后一个ADMIN
        if ("ADMIN".equals(member.getRole())) {
            long adminCount = companyMemberRepository.findByCompanyIdAndRole(member.getCompanyId(), "ADMIN").size();
            if (adminCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the last admin from the company");
            }
        }
        
        companyMemberRepository.delete(member);
    }
    
    /**
     * 转换为DTO
     */
    private CompanyMemberDTO toDTO(CompanyMember member) {
        CompanyMemberDTO dto = new CompanyMemberDTO();
        dto.setId(member.getId());
        dto.setCompanyId(member.getCompanyId());
        dto.setUserEmail(member.getUserEmail());
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());
        dto.setUpdatedAt(member.getUpdatedAt());
        return dto;
    }
}
