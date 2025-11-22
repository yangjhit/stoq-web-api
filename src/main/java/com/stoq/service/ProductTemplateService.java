package com.stoq.service;
import com.stoq.dto.CreateProductTemplateDTO;
import com.stoq.dto.ProductTemplateResponseDTO;
import com.stoq.entity.ProductCategory;
import com.stoq.entity.ProductTemplate;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ProductCategoryRepository;
import com.stoq.repository.ProductTemplateRepository;
import com.stoq.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTemplateService {
    
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 创建商品模板(需要ADMIN权限)
     */
    @Transactional
    public ProductTemplateResponseDTO createProductTemplate(CreateProductTemplateDTO dto, String creatorEmail) {
        // 验证分类是否存在
        ProductCategory category = productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + dto.getCategoryId()));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(category.getClusterId(), creatorEmail);
        
        // 检查模板名称是否已存在(在分类内唯一)
        if (productTemplateRepository.findByCategoryIdAndName(dto.getCategoryId(), dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Template name already exists in this category");
        }
        
        // 创建模板
        ProductTemplate template = new ProductTemplate();
        template.setCategoryId(dto.getCategoryId());
        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        
        // 将图片列表转换为逗号分隔的字符串
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            template.setImages(String.join(",", dto.getImages()));
        }
        
        template.setUnit(dto.getUnit());
        template.setPrice(dto.getPrice());
        template.setCurrency(dto.getCurrency());
        template.setSupplier(dto.getSupplier());
        template.setSupplierCountry(dto.getSupplierCountry());
        template.setBarCode(dto.getBarCode());
        template.setQrCode(dto.getQrCode());
        template.setCreatorEmail(creatorEmail);
        
        ProductTemplate savedTemplate = productTemplateRepository.save(template);
        return toResponseDTO(savedTemplate, category);
    }
    
    /**
     * 获取分类的所有商品模板
     */
    public List<ProductTemplateResponseDTO> getProductTemplatesByCategory(Long categoryId, String userEmail) {
        // 验证分类是否存在
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + categoryId));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(category.getClusterId(), userEmail);
        
        List<ProductTemplate> templates = productTemplateRepository.findByCategoryId(categoryId);
        return templates.stream()
                .map(template -> toResponseDTO(template, category))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取商品模板
     */
    public ProductTemplateResponseDTO getProductTemplateById(Long id, String userEmail) {
        ProductTemplate template = productTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product template not found: " + id));
        
        ProductCategory category = productCategoryRepository.findById(template.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(category.getClusterId(), userEmail);
        
        return toResponseDTO(template, category);
    }
    
    /**
     * 更新商品模板(需要ADMIN权限)
     */
    @Transactional
    public ProductTemplateResponseDTO updateProductTemplate(Long id, CreateProductTemplateDTO dto, String userEmail) {
        ProductTemplate template = productTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product template not found: " + id));
        
        ProductCategory category = productCategoryRepository.findById(template.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(category.getClusterId(), userEmail);
        
        // 检查模板名称是否被其他模板使用
        if (!template.getName().equals(dto.getName())) {
            if (productTemplateRepository.findByCategoryIdAndName(category.getId(), dto.getName()).isPresent()) {
                throw new IllegalArgumentException("Template name already exists in this category");
            }
        }
        
        // 更新模板信息
        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        
        // 将图片列表转换为逗号分隔的字符串
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            template.setImages(String.join(",", dto.getImages()));
        }
        
        template.setUnit(dto.getUnit());
        template.setPrice(dto.getPrice());
        template.setCurrency(dto.getCurrency());
        template.setSupplier(dto.getSupplier());
        template.setSupplierCountry(dto.getSupplierCountry());
        template.setBarCode(dto.getBarCode());
        template.setQrCode(dto.getQrCode());
        
        ProductTemplate updatedTemplate = productTemplateRepository.save(template);
        return toResponseDTO(updatedTemplate, category);
    }
    
    /**
     * 删除商品模板(需要ADMIN权限)
     */
    @Transactional
    public void deleteProductTemplate(Long id, String userEmail) {
        ProductTemplate template = productTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product template not found: " + id));
        
        ProductCategory category = productCategoryRepository.findById(template.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(category.getClusterId(), userEmail);
        
        productTemplateRepository.delete(template);
    }
    
    /**
     * 获取当前用户创建的所有商品模板
     */
    public List<ProductTemplateResponseDTO> getMyProductTemplates(String creatorEmail) {
        List<ProductTemplate> templates = productTemplateRepository.findByCreatorEmail(creatorEmail);
        return templates.stream()
                .map(template -> {
                    ProductCategory category = productCategoryRepository.findById(template.getCategoryId())
                            .orElse(null);
                    return toResponseDTO(template, category);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private ProductTemplateResponseDTO toResponseDTO(ProductTemplate template, ProductCategory category) {
        ProductTemplateResponseDTO dto = new ProductTemplateResponseDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setDescription(template.getDescription());
        
        // 将逗号分隔的字符串转换为列表
        if (template.getImages() != null && !template.getImages().isEmpty()) {
            dto.setImages(Arrays.asList(template.getImages().split(",")));
        }
        
        dto.setUnit(template.getUnit());
        dto.setPrice(template.getPrice());
        dto.setCurrency(template.getCurrency());
        dto.setSupplier(template.getSupplier());
        dto.setSupplierCountry(template.getSupplierCountry());
        dto.setBarCode(template.getBarCode());
        dto.setQrCode(template.getQrCode());
        dto.setCategoryId(template.getCategoryId());
        
        if (category != null) {
            dto.setCategoryName(category.getName());
            dto.setClusterId(category.getClusterId());
        }
        
        dto.setCreatorEmail(template.getCreatorEmail());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }
}
