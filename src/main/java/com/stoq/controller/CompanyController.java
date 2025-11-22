package com.stoq.controller;
import com.stoq.dto.CompanyResponseDTO;
import com.stoq.dto.CreateCompanyDTO;
import com.stoq.service.CompanyService;
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
@RequestMapping("/companies")
@Tag(name = "Company Management", description = "Company creation, query, update, and delete APIs")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    /**
     * Create a new company
     */
    @PostMapping
    @Operation(summary = "Create company", 
               description = "Create a new company (PERSONAL or PROFESSIONAL type). PROFESSIONAL type requires registration number.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CompanyResponseDTO> createCompany(@Validated @RequestBody CreateCompanyDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        CompanyResponseDTO company = companyService.createCompany(dto, ownerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }
    
    /**
     * Get all companies owned by current user
     */
    @GetMapping("/my")
    @Operation(summary = "Get my companies", 
               description = "Get all companies created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CompanyResponseDTO>> getMyCompanies() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        List<CompanyResponseDTO> companies = companyService.getMyCompanies(ownerEmail);
        return ResponseEntity.ok(companies);
    }
    
    /**
     * Get company by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID", 
               description = "Get company details by ID (only accessible by company owner)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        CompanyResponseDTO company = companyService.getCompanyById(id, ownerEmail);
        return ResponseEntity.ok(company);
    }
    
    /**
     * Update company information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update company", 
               description = "Update company information (only accessible by company owner)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable Long id,
            @Validated @RequestBody CreateCompanyDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        CompanyResponseDTO company = companyService.updateCompany(id, dto, ownerEmail);
        return ResponseEntity.ok(company);
    }
    
    /**
     * Delete company
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete company", 
               description = "Delete company (only accessible by company owner)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        companyService.deleteCompany(id, ownerEmail);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all companies (admin only)
     */
    @GetMapping
    @Operation(summary = "Get all companies", 
               description = "Get all companies in the system (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }
}
