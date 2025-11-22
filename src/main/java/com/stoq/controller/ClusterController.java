package com.stoq.controller;
import com.stoq.dto.ClusterResponseDTO;
import com.stoq.dto.CreateClusterDTO;
import com.stoq.service.ClusterService;
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
@RequestMapping("/clusters")
@Tag(name = "Cluster Management", description = "Cluster creation, query, update, and delete APIs")
public class ClusterController {
    
    @Autowired
    private ClusterService clusterService;
    
    // Create a new cluster
    @PostMapping
    @Operation(summary = "Create cluster", 
               description = "Create a new cluster (PERSONAL or PROFESSIONAL type). PROFESSIONAL type requires registration number.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClusterResponseDTO> createCluster(@Validated @RequestBody CreateClusterDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        ClusterResponseDTO cluster = clusterService.createCluster(dto, ownerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(cluster);
    }
    
    // Get all clusters owned by current user
    @GetMapping("/my")
    @Operation(summary = "Get my clusters", 
               description = "Get all clusters created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ClusterResponseDTO>> getMyClusters() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        List<ClusterResponseDTO> clusters = clusterService.getMyClusters(ownerEmail);
        return ResponseEntity.ok(clusters);
    }
    
    // Get cluster by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get cluster by ID", 
               description = "Get cluster details by ID (only accessible by cluster owner)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClusterResponseDTO> getClusterById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        ClusterResponseDTO cluster = clusterService.getClusterById(id, ownerEmail);
        return ResponseEntity.ok(cluster);
    }
    
    // Update cluster information
    @PutMapping("/{id}")
    @Operation(summary = "Update cluster", 
               description = "Update cluster information (only accessible by cluster owner)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClusterResponseDTO> updateCluster(
            @PathVariable Long id,
            @Validated @RequestBody CreateClusterDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        ClusterResponseDTO cluster = clusterService.updateCluster(id, dto, ownerEmail);
        return ResponseEntity.ok(cluster);
    }
    
    // Delete cluster
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cluster", 
               description = "Delete cluster (only accessible by cluster owner)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCluster(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerEmail = authentication.getName();
        clusterService.deleteCluster(id, ownerEmail);
        return ResponseEntity.noContent().build();
    }
    
    // Get all clusters (admin only)
    @GetMapping
    @Operation(summary = "Get all clusters", 
               description = "Get all clusters in the system (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ClusterResponseDTO>> getAllClusters() {
        List<ClusterResponseDTO> clusters = clusterService.getAllClusters();
        return ResponseEntity.ok(clusters);
    }
}
