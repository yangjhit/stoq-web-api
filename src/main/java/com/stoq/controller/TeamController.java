package com.stoq.controller;
import com.stoq.dto.CreateTeamDTO;
import com.stoq.dto.TeamResponseDTO;
import com.stoq.service.TeamService;
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
@RequestMapping("/teams")
@Tag(name = "Team Management", description = "Team creation, query, update, and delete APIs")
public class TeamController {
    
    @Autowired
    private TeamService teamService;
    
    /**
     * Create a new team
     */
    @PostMapping
    @Operation(summary = "Create team", 
               description = "Create a new team for a cluster (requires ADMIN or MANAGER role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TeamResponseDTO> createTeam(@Validated @RequestBody CreateTeamDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        TeamResponseDTO team = teamService.createTeam(dto, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }
    
    /**
     * Get all teams for a specific cluster
     */
    @GetMapping("/cluster/{clusterId}")
    @Operation(summary = "Get teams by cluster", 
               description = "Get all teams for a specific cluster",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByCluster(@PathVariable Long clusterId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<TeamResponseDTO> teams = teamService.getTeamsByCluster(clusterId, userEmail);
        return ResponseEntity.ok(teams);
    }
    
    /**
     * Get all teams created by current user
     */
    @GetMapping("/my")
    @Operation(summary = "Get my teams", 
               description = "Get all teams created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<TeamResponseDTO>> getMyTeams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<TeamResponseDTO> teams = teamService.getMyTeams(userEmail);
        return ResponseEntity.ok(teams);
    }
    
    /**
     * Get team by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", 
               description = "Get team details by ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        TeamResponseDTO team = teamService.getTeamById(id, userEmail);
        return ResponseEntity.ok(team);
    }
    
    /**
     * Update team information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update team", 
               description = "Update team information (requires ADMIN or MANAGER role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @PathVariable Long id,
            @Validated @RequestBody CreateTeamDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        TeamResponseDTO team = teamService.updateTeam(id, dto, userEmail);
        return ResponseEntity.ok(team);
    }
    
    /**
     * Delete team
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team", 
               description = "Delete team (requires ADMIN role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        teamService.deleteTeam(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
