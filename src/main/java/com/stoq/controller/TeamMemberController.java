package com.stoq.controller;
import com.stoq.dto.CreateTeamMemberDTO;
import com.stoq.dto.TeamMemberResponseDTO;
import com.stoq.service.TeamMemberService;
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
@RequestMapping("/team-members")
@Tag(name = "Team Member Management", description = "Team member creation, query, update, and delete APIs")
public class TeamMemberController {
    
    @Autowired
    private TeamMemberService teamMemberService;
    
    /**
     * Add a member to team
     */
    @PostMapping
    @Operation(summary = "Add team member", 
               description = "Add a new member to team (requires ADMIN or MANAGER role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TeamMemberResponseDTO> addTeamMember(@Validated @RequestBody CreateTeamMemberDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creatorEmail = authentication.getName();
        TeamMemberResponseDTO member = teamMemberService.addTeamMember(dto, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }
    
    /**
     * Get all members of a team
     */
    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get team members", 
               description = "Get all members of a team",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<TeamMemberResponseDTO>> getTeamMembers(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<TeamMemberResponseDTO> members = teamMemberService.getTeamMembers(teamId, userEmail);
        return ResponseEntity.ok(members);
    }
    
    /**
     * Get team member by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get team member by ID", 
               description = "Get team member details by ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TeamMemberResponseDTO> getTeamMemberById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        TeamMemberResponseDTO member = teamMemberService.getTeamMemberById(id, userEmail);
        return ResponseEntity.ok(member);
    }
    
    /**
     * Get all team members created by current user
     */
    @GetMapping("/my")
    @Operation(summary = "Get my team members", 
               description = "Get all team members created by current logged-in user",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<TeamMemberResponseDTO>> getMyTeamMembers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String creatorEmail = authentication.getName();
        List<TeamMemberResponseDTO> members = teamMemberService.getMyTeamMembers(creatorEmail);
        return ResponseEntity.ok(members);
    }
    
    /**
     * Update team member information
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update team member", 
               description = "Update team member information (requires ADMIN or MANAGER role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TeamMemberResponseDTO> updateTeamMember(
            @PathVariable Long id,
            @Validated @RequestBody CreateTeamMemberDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        TeamMemberResponseDTO member = teamMemberService.updateTeamMember(id, dto, userEmail);
        return ResponseEntity.ok(member);
    }
    
    /**
     * Delete team member
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team member", 
               description = "Delete team member (requires ADMIN or MANAGER role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        teamMemberService.deleteTeamMember(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
