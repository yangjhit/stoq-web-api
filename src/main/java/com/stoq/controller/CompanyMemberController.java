package com.stoq.controller;
import com.stoq.dto.CompanyMemberDTO;
import com.stoq.service.CompanyMemberService;
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
@RequestMapping("/company-members")
@Tag(name = "Company Member Management", description = "Company member management APIs")
public class CompanyMemberController {
    
    @Autowired
    private CompanyMemberService companyMemberService;
    
    /**
     * Add a member to company
     */
    @PostMapping
    @Operation(summary = "Add company member", 
               description = "Add a new member to company (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CompanyMemberDTO> addMember(@Validated @RequestBody CompanyMemberDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorEmail = authentication.getName();
        CompanyMemberDTO member = companyMemberService.addMember(dto, operatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }
    
    /**
     * Get all members of a company
     */
    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get company members", 
               description = "Get all members of a company",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CompanyMemberDTO>> getCompanyMembers(@PathVariable Long companyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<CompanyMemberDTO> members = companyMemberService.getCompanyMembers(companyId, userEmail);
        return ResponseEntity.ok(members);
    }
    
    /**
     * Update member role
     */
    @PutMapping("/{memberId}/role")
    @Operation(summary = "Update member role", 
               description = "Update member role (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CompanyMemberDTO> updateMemberRole(
            @PathVariable Long memberId,
            @RequestParam String newRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorEmail = authentication.getName();
        CompanyMemberDTO member = companyMemberService.updateMemberRole(memberId, newRole, operatorEmail);
        return ResponseEntity.ok(member);
    }
    
    /**
     * Remove member from company
     */
    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove company member", 
               description = "Remove a member from company (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorEmail = authentication.getName();
        companyMemberService.removeMember(memberId, operatorEmail);
        return ResponseEntity.noContent().build();
    }
}
