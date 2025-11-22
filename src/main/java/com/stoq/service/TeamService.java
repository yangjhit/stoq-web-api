package com.stoq.service;
import com.stoq.dto.CreateTeamDTO;
import com.stoq.dto.TeamResponseDTO;
import com.stoq.entity.Cluster;
import com.stoq.entity.Team;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ClusterRepository;
import com.stoq.repository.TeamRepository;
import com.stoq.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    
    private final TeamRepository teamRepository;
    private final ClusterRepository clusterRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 创建团队(需要ADMIN权限)
     */
    @Transactional
    public TeamResponseDTO createTeam(CreateTeamDTO dto, String creatorEmail) {
        // 验证集群是否存在
        Cluster cluster = clusterRepository.findById(dto.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found: " + dto.getClusterId()));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(dto.getClusterId(), creatorEmail);
        
        // 创建团队实体
        Team team = new Team();
        team.setName(dto.getName());
        team.setLogo(dto.getLogo());
        team.setDescription(dto.getDescription());
        team.setClusterId(dto.getClusterId());
        team.setCreatorEmail(creatorEmail);
        
        // 保存团队
        Team savedTeam = teamRepository.save(team);
        
        return toResponseDTO(savedTeam, cluster.getName());
    }
    
    /**
     * 获取集群的所有团队
     */
    public List<TeamResponseDTO> getTeamsByCluster(Long clusterId, String userEmail) {
        // 验证集群是否存在
        Cluster cluster = clusterRepository.findById(clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found: " + clusterId));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(clusterId, userEmail);
        
        List<Team> teams = teamRepository.findByClusterId(clusterId);
        return teams.stream()
                .map(team -> toResponseDTO(team, cluster.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取当前用户创建的所有团队
     */
    public List<TeamResponseDTO> getMyTeams(String creatorEmail) {
        List<Team> teams = teamRepository.findByCreatorEmail(creatorEmail);
        return teams.stream()
                .map(team -> {
                    String clusterName = clusterRepository.findById(team.getClusterId())
                            .map(Cluster::getName)
                            .orElse("Unknown");
                    return toResponseDTO(team, clusterName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取团队
     */
    public TeamResponseDTO getTeamById(Long id, String userEmail) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(team.getClusterId(), userEmail);
        
        Cluster cluster = clusterRepository.findById(team.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found"));
        
        return toResponseDTO(team, cluster.getName());
    }
    
    /**
     * 更新团队信息(需要ADMIN权限)
     */
    @Transactional
    public TeamResponseDTO updateTeam(Long id, CreateTeamDTO dto, String userEmail) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(team.getClusterId(), userEmail);
        
        Cluster cluster = clusterRepository.findById(team.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found"));
        
        // 更新团队信息
        team.setName(dto.getName());
        team.setLogo(dto.getLogo());
        team.setDescription(dto.getDescription());
        
        Team updatedTeam = teamRepository.save(team);
        return toResponseDTO(updatedTeam, cluster.getName());
    }
    
    /**
     * 删除团队(需要ADMIN权限)
     */
    @Transactional
    public void deleteTeam(Long id, String userEmail) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(team.getClusterId(), userEmail);
        
        teamRepository.delete(team);
    }
    
    /**
     * 转换为响应DTO
     */
    private TeamResponseDTO toResponseDTO(Team team, String clusterName) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLogo(team.getLogo());
        dto.setDescription(team.getDescription());
        dto.setClusterId(team.getClusterId());
        dto.setClusterName(clusterName);
        dto.setCreatorEmail(team.getCreatorEmail());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());
        return dto;
    }
}
