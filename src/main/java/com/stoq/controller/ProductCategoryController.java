package com.stoq.controller;
import com.stoq.dto.CreateProductCategoryDTO;
import com.stoq.dto.ProductCategoryResponseDTO;
import com.stoq.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-categories")
@Tag(name = "Product Category Management", description = "Product category creation, query, update, and delete APIs")
public class ProductCategoryController {
    
    @Autowired
    private ProductCategoryService productCategoryService;
    
    /**
     * Create a new product category
     */
    @PostMapping
    @Operation(summary = "Create product category", 
               description = "Create a new product category for a cluster (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductCategoryResponseDTO> createProductCategory(@Validated @RequestBody CreateProductCategoryDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creatorEmail = authentication.getName();
        ProductCategoryResponseDTO category = productCategoryService.createProductCategory(dto, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    /**
     * Get all product categories for a specific cluster
     */
    @GetMapping("/cluster/{clusterId}")
    @Operation(summary = "Get product categories by cluster", 
               description = "Get all product categories for a specific cluster",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ProductCategoryResponseDTO>> getProductCategoriesByCluster(@PathVariable Long clusterId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<ProductCategoryResponseDTO> categories = productCategoryService.getProductCategoriesByCluster(clusterId, userEmail);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get product category by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product category by ID", 
               description = "Get product category details by ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductCategoryResponseDTO> getProductCategoryById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        ProductCategoryResponseDTO category = productCategoryService.getProductCategoryById(id, userEmail);
        return ResponseEntity.ok(category);
    }
    
    /**
     * Get all product categories created by current user
     */
    @GetMapping("/my")
    @Operation(summary = "Get my product categories", 
               description = "Get all product categories created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ProductCategoryResponseDTO>> getMyProductCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creatorEmail = authentication.getName();
        List<ProductCategoryResponseDTO> categories = productCategoryService.getMyProductCategories(creatorEmail);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Update product category information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product category", 
               description = "Update product category information (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductCategoryResponseDTO> updateProductCategory(
            @PathVariable Long id,
            @Validated @RequestBody CreateProductCategoryDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        ProductCategoryResponseDTO category = productCategoryService.updateProductCategory(id, dto, userEmail);
        return ResponseEntity.ok(category);
    }
    
    /**
     * Delete product category
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product category", 
               description = "Delete product category (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteProductCategory(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        productCategoryService.deleteProductCategory(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
