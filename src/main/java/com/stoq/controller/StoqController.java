package com.stoq.controller;
import com.stoq.dto.CreateStoqDTO;
import com.stoq.dto.StoqResponseDTO;
import com.stoq.service.StoqService;
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
@RequestMapping("/stoqs")
@Tag(name = "Stoq Management", description = "Stoq (warehouse) creation, query, update, and delete APIs")
public class StoqController {
    
    @Autowired
    private StoqService stoqService;
    
    /**
     * Create a new stoq
     */
    @PostMapping
    @Operation(summary = "Create stoq", 
               description = "Create a new stoq (warehouse) for a company",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<StoqResponseDTO> createStoq(@Validated @RequestBody CreateStoqDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        StoqResponseDTO stoq = stoqService.createStoq(dto, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(stoq);
    }
    
    /**
     * Get all stoqs for a specific company
     */
    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get stoqs by company", 
               description = "Get all stoqs for a specific company",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<StoqResponseDTO>> getStoqsByCompany(@PathVariable Long companyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<StoqResponseDTO> stoqs = stoqService.getStoqsByCompany(companyId, userEmail);
        return ResponseEntity.ok(stoqs);
    }
    
    /**
     * Get all stoqs created by current user
     */
    @GetMapping("/my")
    @Operation(summary = "Get my stoqs", 
               description = "Get all stoqs created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<StoqResponseDTO>> getMyStoqs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<StoqResponseDTO> stoqs = stoqService.getMyStoqs(userEmail);
        return ResponseEntity.ok(stoqs);
    }
    
    /**
     * Get stoq by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get stoq by ID", 
               description = "Get stoq details by ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<StoqResponseDTO> getStoqById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        StoqResponseDTO stoq = stoqService.getStoqById(id, userEmail);
        return ResponseEntity.ok(stoq);
    }
    
    /**
     * Update stoq information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update stoq", 
               description = "Update stoq information",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<StoqResponseDTO> updateStoq(
            @PathVariable Long id,
            @Validated @RequestBody CreateStoqDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        StoqResponseDTO stoq = stoqService.updateStoq(id, dto, userEmail);
        return ResponseEntity.ok(stoq);
    }
    
    /**
     * Delete stoq
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stoq", 
               description = "Delete stoq",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteStoq(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        stoqService.deleteStoq(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
