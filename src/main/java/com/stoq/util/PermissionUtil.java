package com.stoq.util;
import com.stoq.entity.Company;
import com.stoq.entity.CompanyMember;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.CompanyMemberRepository;
import com.stoq.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 权限检查工具类
 */
@Component
@RequiredArgsConstructor
public class PermissionUtil {
    
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    
    /**
     * 检查用户是否是公司的ADMIN
     */
    public boolean isCompanyAdmin(Long companyId, String userEmail) {
        return companyMemberRepository.findByCompanyIdAndUserEmail(companyId, userEmail)
                .map(member -> "ADMIN".equals(member.getRole()))
                .orElse(false);
    }
    
    /**
     * 检查用户是否是公司的ADMIN或MEMBER
     */
    public boolean isCompanyAdminOrMember(Long companyId, String userEmail) {
        return companyMemberRepository.findByCompanyIdAndUserEmail(companyId, userEmail)
                .isPresent();
    }
    
    /**
     * 检查用户是否是公司成员
     */
    public boolean isCompanyMember(Long companyId, String userEmail) {
        return companyMemberRepository.findByCompanyIdAndUserEmail(companyId, userEmail)
                .isPresent();
    }
    
    /**
     * 获取用户在公司中的角色
     */
    public String getUserRoleInCompany(Long companyId, String userEmail) {
        return companyMemberRepository.findByCompanyIdAndUserEmail(companyId, userEmail)
                .map(CompanyMember::getRole)
                .orElse(null);
    }
    
    /**
     * 验证用户是否有权限访问公司(必须是ADMIN)
     */
    public void verifyCompanyAdmin(Long companyId, String userEmail) {
        if (!isCompanyAdmin(companyId, userEmail)) {
            throw new ResourceNotFoundException("You don't have permission to access this company");
        }
    }
    
    /**
     * 验证用户是否有权限访问公司(ADMIN或MEMBER)
     */
    public void verifyCompanyAdminOrMember(Long companyId, String userEmail) {
        if (!isCompanyAdminOrMember(companyId, userEmail)) {
            throw new ResourceNotFoundException("You don't have permission to access this company");
        }
    }
    
    /**
     * 验证用户是否是公司成员
     */
    public void verifyCompanyMember(Long companyId, String userEmail) {
        if (!isCompanyMember(companyId, userEmail)) {
            throw new ResourceNotFoundException("You are not a member of this company");
        }
    }
}
