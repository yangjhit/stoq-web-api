package com.stoq.controller;
import com.stoq.dto.ClusterMemberDTO;
import com.stoq.service.ClusterMemberService;
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
@RequestMapping("/cluster-members")
@Tag(name = "Cluster Member Management", description = "Cluster member management APIs")
public class ClusterMemberController {
    
    @Autowired
    private ClusterMemberService clusterMemberService;
    
    // Add a member to cluster
    @PostMapping
    @Operation(summary = "Add cluster member", 
               description = "Add a new member to cluster (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClusterMemberDTO> addMember(@Validated @RequestBody ClusterMemberDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorEmail = authentication.getName();
        ClusterMemberDTO member = clusterMemberService.addMember(dto, operatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }
    
    // Get all members of a cluster
    @GetMapping("/cluster/{clusterId}")
    @Operation(summary = "Get cluster members", 
               description = "Get all members of a cluster",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ClusterMemberDTO>> getClusterMembers(@PathVariable Long clusterId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<ClusterMemberDTO> members = clusterMemberService.getClusterMembers(clusterId, userEmail);
        return ResponseEntity.ok(members);
    }
    
    // Update member role
    @PutMapping("/{memberId}/role")
    @Operation(summary = "Update member role", 
               description = "Update member role (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClusterMemberDTO> updateMemberRole(
            @PathVariable Long memberId,
            @RequestParam String newRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorEmail = authentication.getName();
        ClusterMemberDTO member = clusterMemberService.updateMemberRole(memberId, newRole, operatorEmail);
        return ResponseEntity.ok(member);
    }
    
    // Remove member from cluster
    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove cluster member", 
               description = "Remove a member from cluster (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorEmail = authentication.getName();
        clusterMemberService.removeMember(memberId, operatorEmail);
        return ResponseEntity.noContent().build();
    }
}
