package com.stoq.service;
import com.stoq.dto.CreateProductCategoryDTO;
import com.stoq.dto.ProductCategoryResponseDTO;
import com.stoq.entity.Company;
import com.stoq.entity.ProductCategory;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.CompanyRepository;
import com.stoq.repository.ProductCategoryRepository;
import com.stoq.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    
    private final ProductCategoryRepository productCategoryRepository;
    private final CompanyRepository companyRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 创建商品分类(需要ADMIN权限)
     */
    @Transactional
    public ProductCategoryResponseDTO createProductCategory(CreateProductCategoryDTO dto, String creatorEmail) {
        // 验证公司是否存在
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + dto.getCompanyId()));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(dto.getCompanyId(), creatorEmail);
        
        // 检查分类名称是否已存在(在公司内唯一)
        if (productCategoryRepository.findByCompanyIdAndName(dto.getCompanyId(), dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Category name already exists in this company");
        }
        
        // 创建分类
        ProductCategory category = new ProductCategory();
        category.setCompanyId(dto.getCompanyId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImage(dto.getImage());
        category.setCreatorEmail(creatorEmail);
        
        ProductCategory savedCategory = productCategoryRepository.save(category);
        return toResponseDTO(savedCategory, company.getName());
    }
    
    /**
     * 获取公司的所有商品分类
     */
    public List<ProductCategoryResponseDTO> getProductCategoriesByCompany(Long companyId, String userEmail) {
        // 验证公司是否存在
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        
        // 验证用户是否是公司成员
        permissionUtil.verifyCompanyMember(companyId, userEmail);
        
        List<ProductCategory> categories = productCategoryRepository.findByCompanyId(companyId);
        return categories.stream()
                .map(category -> toResponseDTO(category, company.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取商品分类
     */
    public ProductCategoryResponseDTO getProductCategoryById(Long id, String userEmail) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + id));
        
        // 验证用户是否是公司成员
        permissionUtil.verifyCompanyMember(category.getCompanyId(), userEmail);
        
        Company company = companyRepository.findById(category.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        return toResponseDTO(category, company.getName());
    }
    
    /**
     * 更新商品分类(需要ADMIN权限)
     */
    @Transactional
    public ProductCategoryResponseDTO updateProductCategory(Long id, CreateProductCategoryDTO dto, String userEmail) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(category.getCompanyId(), userEmail);
        
        // 检查分类名称是否被其他分类使用
        if (!category.getName().equals(dto.getName())) {
            if (productCategoryRepository.findByCompanyIdAndName(category.getCompanyId(), dto.getName()).isPresent()) {
                throw new IllegalArgumentException("Category name already exists in this company");
            }
        }
        
        Company company = companyRepository.findById(category.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        // 更新分类信息
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImage(dto.getImage());
        
        ProductCategory updatedCategory = productCategoryRepository.save(category);
        return toResponseDTO(updatedCategory, company.getName());
    }
    
    /**
     * 删除商品分类(需要ADMIN权限)
     */
    @Transactional
    public void deleteProductCategory(Long id, String userEmail) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(category.getCompanyId(), userEmail);
        
        productCategoryRepository.delete(category);
    }
    
    /**
     * 获取当前用户创建的所有商品分类
     */
    public List<ProductCategoryResponseDTO> getMyProductCategories(String creatorEmail) {
        List<ProductCategory> categories = productCategoryRepository.findByCreatorEmail(creatorEmail);
        return categories.stream()
                .map(category -> {
                    String companyName = companyRepository.findById(category.getCompanyId())
                            .map(Company::getName)
                            .orElse("Unknown");
                    return toResponseDTO(category, companyName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private ProductCategoryResponseDTO toResponseDTO(ProductCategory category, String companyName) {
        ProductCategoryResponseDTO dto = new ProductCategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImage(category.getImage());
        dto.setCompanyId(category.getCompanyId());
        dto.setCompanyName(companyName);
        dto.setCreatorEmail(category.getCreatorEmail());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
