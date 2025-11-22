package com.stoq.service;
import com.stoq.dto.CompanyResponseDTO;
import com.stoq.dto.CreateCompanyDTO;
import com.stoq.entity.Company;
import com.stoq.entity.CompanyMember;
import com.stoq.enums.CompanyType;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.CompanyMemberRepository;
import com.stoq.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    
    /**
     * 创建公司
     */
    @Transactional
    public CompanyResponseDTO createCompany(CreateCompanyDTO dto, String ownerEmail) {
        // 验证PROFESSIONAL类型必须提供注册号
        if ("PROFESSIONAL".equals(dto.getType())) {
            if (dto.getRegistrationNumber() == null || dto.getRegistrationNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required for PROFESSIONAL company type");
            }
        }
        
        // 验证公司类型
        CompanyType.fromCode(dto.getType());
        
        // 创建公司实体
        Company company = new Company();
        company.setName(dto.getName());
        company.setLogo(dto.getLogo());
        company.setAddress(dto.getAddress());
        company.setCountryId(dto.getCountryId());
        company.setCity(dto.getCity());
        company.setField(dto.getField());
        company.setEmployeeCount(dto.getEmployeeCount());
        company.setType(dto.getType());
        company.setRegistrationNumber(dto.getRegistrationNumber());
        company.setOwnerEmail(ownerEmail);
        
        // 保存公司
        Company savedCompany = companyRepository.save(company);
        
        // 将创建者添加为ADMIN
        CompanyMember adminMember = new CompanyMember();
        adminMember.setCompanyId(savedCompany.getId());
        adminMember.setUserEmail(ownerEmail);
        adminMember.setRole("ADMIN");
        companyMemberRepository.save(adminMember);
        
        return toResponseDTO(savedCompany);
    }
    
    /**
     * 获取当前用户的所有公司
     */
    public List<CompanyResponseDTO> getMyCompanies(String ownerEmail) {
        List<Company> companies = companyRepository.findByOwnerEmail(ownerEmail);
        return companies.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取公司(仅限创建者)
     */
    public CompanyResponseDTO getCompanyById(Long id, String ownerEmail) {
        Company company = companyRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found or you don't have permission: " + id));
        return toResponseDTO(company);
    }
    
    /**
     * 更新公司信息
     */
    @Transactional
    public CompanyResponseDTO updateCompany(Long id, CreateCompanyDTO dto, String ownerEmail) {
        Company company = companyRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found or you don't have permission: " + id));
        
        // 验证PROFESSIONAL类型必须提供注册号
        if ("PROFESSIONAL".equals(dto.getType())) {
            if (dto.getRegistrationNumber() == null || dto.getRegistrationNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required for PROFESSIONAL company type");
            }
        }
        
        // 验证公司类型
        CompanyType.fromCode(dto.getType());
        
        // 更新公司信息
        company.setName(dto.getName());
        company.setLogo(dto.getLogo());
        company.setAddress(dto.getAddress());
        company.setCountryId(dto.getCountryId());
        company.setCity(dto.getCity());
        company.setField(dto.getField());
        company.setEmployeeCount(dto.getEmployeeCount());
        company.setType(dto.getType());
        company.setRegistrationNumber(dto.getRegistrationNumber());
        
        Company updatedCompany = companyRepository.save(company);
        return toResponseDTO(updatedCompany);
    }
    
    /**
     * 删除公司
     */
    @Transactional
    public void deleteCompany(Long id, String ownerEmail) {
        Company company = companyRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found or you don't have permission: " + id));
        
        // 先删除公司成员
        companyMemberRepository.deleteByCompanyId(id);
        
        // 再删除公司
        companyRepository.delete(company);
    }
    
    /**
     * 获取所有公司(管理员功能)
     */
    public List<CompanyResponseDTO> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private CompanyResponseDTO toResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setLogo(company.getLogo());
        dto.setAddress(company.getAddress());
        dto.setCountryId(company.getCountryId());
        dto.setCity(company.getCity());
        dto.setField(company.getField());
        dto.setEmployeeCount(company.getEmployeeCount());
        dto.setType(company.getType());
        dto.setRegistrationNumber(company.getRegistrationNumber());
        dto.setOwnerEmail(company.getOwnerEmail());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }
}
