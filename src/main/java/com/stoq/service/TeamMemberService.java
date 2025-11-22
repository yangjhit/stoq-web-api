package com.stoq.service;

import com.stoq.dto.CreateTeamMemberDTO;
import com.stoq.dto.TeamMemberResponseDTO;
import com.stoq.entity.Team;
import com.stoq.entity.TeamMember;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.TeamMemberRepository;
import com.stoq.repository.TeamRepository;
import com.stoq.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 添加团队成员(需要ADMIN权限)
     * 邮箱作为唯一标识,不自动关联用户
     */
    @Transactional
    public TeamMemberResponseDTO addTeamMember(CreateTeamMemberDTO dto, String creatorEmail) {
        // 验证团队是否存在
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + dto.getTeamId()));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(team.getClusterId(), creatorEmail);
        
        // 检查邮箱是否全局唯一
        if (teamMemberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // 创建团队成员
        TeamMember member = new TeamMember();
        member.setTeamId(dto.getTeamId());
        member.setAvatar(dto.getAvatar());
        member.setName(dto.getName());
        member.setSurname(dto.getSurname());
        member.setDescription(dto.getDescription());
        member.setCountryId(dto.getCountryId());
        member.setCity(dto.getCity());
        member.setStoq(dto.getStoq());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setRole(dto.getRole());
        member.setCreatorEmail(creatorEmail);
        
        TeamMember savedMember = teamMemberRepository.save(member);
        return toResponseDTO(savedMember, team.getName());
    }
    
    /**
     * 获取团队的所有成员
     */
    public List<TeamMemberResponseDTO> getTeamMembers(Long teamId, String userEmail) {
        // 验证团队是否存在
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(team.getClusterId(), userEmail);
        
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        return members.stream()
                .map(member -> toResponseDTO(member, team.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取团队成员
     */
    public TeamMemberResponseDTO getTeamMemberById(Long id, String userEmail) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found: " + id));
        
        Team team = teamRepository.findById(member.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(team.getClusterId(), userEmail);
        
        return toResponseDTO(member, team.getName());
    }
    
    /**
     * 更新团队成员信息(需要ADMIN权限)
     */
    @Transactional
    public TeamMemberResponseDTO updateTeamMember(Long id, CreateTeamMemberDTO dto, String userEmail) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found: " + id));
        
        Team team = teamRepository.findById(member.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(team.getClusterId(), userEmail);
        
        // 检查邮箱是否被其他成员使用
        if (!member.getEmail().equals(dto.getEmail())) {
            if (teamMemberRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        
        // 更新成员信息
        member.setAvatar(dto.getAvatar());
        member.setName(dto.getName());
        member.setSurname(dto.getSurname());
        member.setDescription(dto.getDescription());
        member.setCountryId(dto.getCountryId());
        member.setCity(dto.getCity());
        member.setStoq(dto.getStoq());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setRole(dto.getRole());
        
        TeamMember updatedMember = teamMemberRepository.save(member);
        return toResponseDTO(updatedMember, team.getName());
    }
    
    /**
     * 删除团队成员(需要ADMIN权限)
     */
    @Transactional
    public void deleteTeamMember(Long id, String userEmail) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found: " + id));
        
        Team team = teamRepository.findById(member.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(team.getClusterId(), userEmail);
        
        teamMemberRepository.delete(member);
    }
    
    /**
     * 获取当前用户创建的所有团队成员
     */
    public List<TeamMemberResponseDTO> getMyTeamMembers(String creatorEmail) {
        List<TeamMember> members = teamMemberRepository.findByCreatorEmail(creatorEmail);
        return members.stream()
                .map(member -> {
                    String teamName = teamRepository.findById(member.getTeamId())
                            .map(Team::getName)
                            .orElse("Unknown");
                    return toResponseDTO(member, teamName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private TeamMemberResponseDTO toResponseDTO(TeamMember member, String teamName) {
        TeamMemberResponseDTO dto = new TeamMemberResponseDTO();
        dto.setId(member.getId());
        dto.setAvatar(member.getAvatar());
        dto.setName(member.getName());
        dto.setSurname(member.getSurname());
        dto.setDescription(member.getDescription());
        dto.setCountryId(member.getCountryId());
        dto.setCity(member.getCity());
        dto.setStoq(member.getStoq());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());
        dto.setRole(member.getRole());
        dto.setLinkedUserEmail(member.getLinkedUserEmail());
        dto.setTeamId(member.getTeamId());
        dto.setTeamName(teamName);
        dto.setCreatorEmail(member.getCreatorEmail());
        dto.setCreatedAt(member.getCreatedAt());
        dto.setUpdatedAt(member.getUpdatedAt());
        return dto;
    }
}
