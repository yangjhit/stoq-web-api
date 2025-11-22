package com.stoq.service;
import com.stoq.dto.CreateProductCategoryDTO;
import com.stoq.dto.ProductCategoryResponseDTO;
import com.stoq.entity.Cluster;
import com.stoq.entity.ProductCategory;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ClusterRepository;
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
    private final ClusterRepository clusterRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 创建商品分类(需要ADMIN权限)
     */
    @Transactional
    public ProductCategoryResponseDTO createProductCategory(CreateProductCategoryDTO dto, String creatorEmail) {
        // 验证集群是否存在
        Cluster cluster = clusterRepository.findById(dto.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found: " + dto.getClusterId()));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(dto.getClusterId(), creatorEmail);
        
        // 检查分类名称是否已存在(在集群内唯一)
        if (productCategoryRepository.findByClusterIdAndName(dto.getClusterId(), dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Category name already exists in this cluster");
        }
        
        // 创建分类
        ProductCategory category = new ProductCategory();
        category.setClusterId(dto.getClusterId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImage(dto.getImage());
        category.setCreatorEmail(creatorEmail);
        
        ProductCategory savedCategory = productCategoryRepository.save(category);
        return toResponseDTO(savedCategory, cluster.getName());
    }
    
    /**
     * 获取集群的所有商品分类
     */
    public List<ProductCategoryResponseDTO> getProductCategoriesByCluster(Long clusterId, String userEmail) {
        // 验证集群是否存在
        Cluster cluster = clusterRepository.findById(clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found: " + clusterId));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(clusterId, userEmail);
        
        List<ProductCategory> categories = productCategoryRepository.findByClusterId(clusterId);
        return categories.stream()
                .map(category -> toResponseDTO(category, cluster.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取商品分类
     */
    public ProductCategoryResponseDTO getProductCategoryById(Long id, String userEmail) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + id));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(category.getClusterId(), userEmail);
        
        Cluster cluster = clusterRepository.findById(category.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found"));
        
        return toResponseDTO(category, cluster.getName());
    }
    
    /**
     * 更新商品分类(需要ADMIN权限)
     */
    @Transactional
    public ProductCategoryResponseDTO updateProductCategory(Long id, CreateProductCategoryDTO dto, String userEmail) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(category.getClusterId(), userEmail);
        
        // 检查分类名称是否被其他分类使用
        if (!category.getName().equals(dto.getName())) {
            if (productCategoryRepository.findByClusterIdAndName(category.getClusterId(), dto.getName()).isPresent()) {
                throw new IllegalArgumentException("Category name already exists in this cluster");
            }
        }
        
        Cluster cluster = clusterRepository.findById(category.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found"));
        
        // 更新分类信息
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImage(dto.getImage());
        
        ProductCategory updatedCategory = productCategoryRepository.save(category);
        return toResponseDTO(updatedCategory, cluster.getName());
    }
    
    /**
     * 删除商品分类(需要ADMIN权限)
     */
    @Transactional
    public void deleteProductCategory(Long id, String userEmail) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(category.getClusterId(), userEmail);
        
        productCategoryRepository.delete(category);
    }
    
    /**
     * 获取当前用户创建的所有商品分类
     */
    public List<ProductCategoryResponseDTO> getMyProductCategories(String creatorEmail) {
        List<ProductCategory> categories = productCategoryRepository.findByCreatorEmail(creatorEmail);
        return categories.stream()
                .map(category -> {
                    String clusterName = clusterRepository.findById(category.getClusterId())
                            .map(Cluster::getName)
                            .orElse("Unknown");
                    return toResponseDTO(category, clusterName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private ProductCategoryResponseDTO toResponseDTO(ProductCategory category, String clusterName) {
        ProductCategoryResponseDTO dto = new ProductCategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImage(category.getImage());
        dto.setClusterId(category.getClusterId());
        dto.setClusterName(clusterName);
        dto.setCreatorEmail(category.getCreatorEmail());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
