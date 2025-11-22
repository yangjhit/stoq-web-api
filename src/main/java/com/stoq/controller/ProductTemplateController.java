package com.stoq.controller;
import com.stoq.dto.CreateProductTemplateDTO;
import com.stoq.dto.ProductTemplateResponseDTO;
import com.stoq.service.ProductTemplateService;
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
@RequestMapping("/product-templates")
@Tag(name = "Product Template Management", description = "Product template creation, query, update, and delete APIs")
public class ProductTemplateController {
    
    @Autowired
    private ProductTemplateService productTemplateService;
    
    /**
     * Create a new product template
     */
    @PostMapping
    @Operation(summary = "Create product template", 
               description = "Create a new product template for a category (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductTemplateResponseDTO> createProductTemplate(@Validated @RequestBody CreateProductTemplateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creatorEmail = authentication.getName();
        ProductTemplateResponseDTO template = productTemplateService.createProductTemplate(dto, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(template);
    }
    
    /**
     * Get all product templates for a specific category
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get product templates by category", 
               description = "Get all product templates for a specific category",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ProductTemplateResponseDTO>> getProductTemplatesByCategory(@PathVariable Long categoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<ProductTemplateResponseDTO> templates = productTemplateService.getProductTemplatesByCategory(categoryId, userEmail);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * Get product template by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product template by ID", 
               description = "Get product template details by ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductTemplateResponseDTO> getProductTemplateById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        ProductTemplateResponseDTO template = productTemplateService.getProductTemplateById(id, userEmail);
        return ResponseEntity.ok(template);
    }
    
    /**
     * Get all product templates created by current user
     */
    @GetMapping("/my")
    @Operation(summary = "Get my product templates", 
               description = "Get all product templates created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ProductTemplateResponseDTO>> getMyProductTemplates() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creatorEmail = authentication.getName();
        List<ProductTemplateResponseDTO> templates = productTemplateService.getMyProductTemplates(creatorEmail);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * Update product template information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product template", 
               description = "Update product template information (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductTemplateResponseDTO> updateProductTemplate(
            @PathVariable Long id,
            @Validated @RequestBody CreateProductTemplateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        ProductTemplateResponseDTO template = productTemplateService.updateProductTemplate(id, dto, userEmail);
        return ResponseEntity.ok(template);
    }
    
    /**
     * Delete product template
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product template", 
               description = "Delete product template (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteProductTemplate(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        productTemplateService.deleteProductTemplate(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
