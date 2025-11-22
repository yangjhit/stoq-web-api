package com.stoq.service;
import com.stoq.dto.CreateStoqDTO;
import com.stoq.dto.StoqResponseDTO;
import com.stoq.entity.Company;
import com.stoq.entity.Stoq;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.CompanyRepository;
import com.stoq.repository.StoqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoqService {
    
    private final StoqRepository stoqRepository;
    private final CompanyRepository companyRepository;
    
    /**
     * 创建仓库
     */
    @Transactional
    public StoqResponseDTO createStoq(CreateStoqDTO dto, String creatorEmail) {
        // 验证公司是否存在且属于当前用户
        Company company = companyRepository.findByIdAndOwnerEmail(dto.getCompanyId(), creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found or you don't have permission: " + dto.getCompanyId()));
        
        // 创建仓库实体
        Stoq stoq = new Stoq();
        stoq.setName(dto.getName());
        stoq.setDescription(dto.getDescription());
        stoq.setAdministrator(dto.getAdministrator());
        stoq.setCompanyId(dto.getCompanyId());
        stoq.setCreatorEmail(creatorEmail);
        
        // 保存仓库
        Stoq savedStoq = stoqRepository.save(stoq);
        
        return toResponseDTO(savedStoq, company.getName());
    }
    
    /**
     * 获取指定公司的所有仓库
     */
    public List<StoqResponseDTO> getStoqsByCompany(Long companyId, String userEmail) {
        // 验证公司是否存在且属于当前用户
        Company company = companyRepository.findByIdAndOwnerEmail(companyId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company not found or you don't have permission: " + companyId));
        
        List<Stoq> stoqs = stoqRepository.findByCompanyId(companyId);
        return stoqs.stream()
                .map(stoq -> toResponseDTO(stoq, company.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取当前用户创建的所有仓库
     */
    public List<StoqResponseDTO> getMyStoqs(String creatorEmail) {
        List<Stoq> stoqs = stoqRepository.findByCreatorEmail(creatorEmail);
        return stoqs.stream()
                .map(stoq -> {
                    String companyName = companyRepository.findById(stoq.getCompanyId())
                            .map(Company::getName)
                            .orElse("Unknown");
                    return toResponseDTO(stoq, companyName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取仓库
     */
    public StoqResponseDTO getStoqById(Long id, String userEmail) {
        Stoq stoq = stoqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stoq not found: " + id));
        
        // 验证用户是否有权限访问(必须是公司所有者)
        Company company = companyRepository.findByIdAndOwnerEmail(stoq.getCompanyId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You don't have permission to access this stoq"));
        
        return toResponseDTO(stoq, company.getName());
    }
    
    /**
     * 更新仓库信息
     */
    @Transactional
    public StoqResponseDTO updateStoq(Long id, CreateStoqDTO dto, String userEmail) {
        Stoq stoq = stoqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stoq not found: " + id));
        
        // 验证用户是否有权限(必须是公司所有者)
        Company company = companyRepository.findByIdAndOwnerEmail(stoq.getCompanyId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You don't have permission to update this stoq"));
        
        // 如果要更改公司,验证新公司是否属于当前用户
        if (!stoq.getCompanyId().equals(dto.getCompanyId())) {
            Company newCompany = companyRepository.findByIdAndOwnerEmail(dto.getCompanyId(), userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "New company not found or you don't have permission: " + dto.getCompanyId()));
            company = newCompany;
        }
        
        // 更新仓库信息
        stoq.setName(dto.getName());
        stoq.setDescription(dto.getDescription());
        stoq.setAdministrator(dto.getAdministrator());
        stoq.setCompanyId(dto.getCompanyId());
        
        Stoq updatedStoq = stoqRepository.save(stoq);
        return toResponseDTO(updatedStoq, company.getName());
    }
    
    /**
     * 删除仓库
     */
    @Transactional
    public void deleteStoq(Long id, String userEmail) {
        Stoq stoq = stoqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stoq not found: " + id));
        
        // 验证用户是否有权限(必须是公司所有者)
        companyRepository.findByIdAndOwnerEmail(stoq.getCompanyId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You don't have permission to delete this stoq"));
        
        stoqRepository.delete(stoq);
    }
    
    /**
     * 转换为响应DTO
     */
    private StoqResponseDTO toResponseDTO(Stoq stoq, String companyName) {
        StoqResponseDTO dto = new StoqResponseDTO();
        dto.setId(stoq.getId());
        dto.setName(stoq.getName());
        dto.setDescription(stoq.getDescription());
        dto.setAdministrator(stoq.getAdministrator());
        dto.setCompanyId(stoq.getCompanyId());
        dto.setCompanyName(companyName);
        dto.setCreatorEmail(stoq.getCreatorEmail());
        dto.setCreatedAt(stoq.getCreatedAt());
        dto.setUpdatedAt(stoq.getUpdatedAt());
        return dto;
    }
}
