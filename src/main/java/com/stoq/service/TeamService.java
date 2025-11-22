package com.stoq.service;
import com.stoq.dto.CreateTeamDTO;
import com.stoq.dto.TeamResponseDTO;
import com.stoq.entity.Company;
import com.stoq.entity.Team;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 创建团队(需要ADMIN权限)
     */
    @Transactional
    public TeamResponseDTO createTeam(CreateTeamDTO dto, String creatorEmail) {
        // 验证公司是否存在
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + dto.getCompanyId()));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(dto.getCompanyId(), creatorEmail);
        
        // 创建团队实体
        Team team = new Team();
        team.setName(dto.getName());
        team.setLogo(dto.getLogo());
        team.setDescription(dto.getDescription());
        team.setCompanyId(dto.getCompanyId());
        team.setCreatorEmail(creatorEmail);
        
        // 保存团队
        Team savedTeam = teamRepository.save(team);
        
        return toResponseDTO(savedTeam, company.getName());
    }
    
    /**
     * 获取公司的所有团队
     */
    public List<TeamResponseDTO> getTeamsByCompany(Long companyId, String userEmail) {
        // 验证公司是否存在
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId));
        
        // 验证用户是否是公司成员
        permissionUtil.verifyCompanyMember(companyId, userEmail);
        
        List<Team> teams = teamRepository.findByCompanyId(companyId);
        return teams.stream()
                .map(team -> toResponseDTO(team, company.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取当前用户创建的所有团队
     */
    public List<TeamResponseDTO> getMyTeams(String creatorEmail) {
        List<Team> teams = teamRepository.findByCreatorEmail(creatorEmail);
        return teams.stream()
                .map(team -> {
                    String companyName = companyRepository.findById(team.getCompanyId())
                            .map(Company::getName)
                            .orElse("Unknown");
                    return toResponseDTO(team, companyName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取团队
     */
    public TeamResponseDTO getTeamById(Long id, String userEmail) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        
        // 验证用户是否是公司成员
        permissionUtil.verifyCompanyMember(team.getCompanyId(), userEmail);
        
        Company company = companyRepository.findById(team.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        return toResponseDTO(team, company.getName());
    }
    
    /**
     * 更新团队信息(需要ADMIN权限)
     */
    @Transactional
    public TeamResponseDTO updateTeam(Long id, CreateTeamDTO dto, String userEmail) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(team.getCompanyId(), userEmail);
        
        Company company = companyRepository.findById(team.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        
        // 更新团队信息
        team.setName(dto.getName());
        team.setLogo(dto.getLogo());
        team.setDescription(dto.getDescription());
        
        Team updatedTeam = teamRepository.save(team);
        return toResponseDTO(updatedTeam, company.getName());
    }
    
    /**
     * 删除团队(需要ADMIN权限)
     */
    @Transactional
    public void deleteTeam(Long id, String userEmail) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        
        // 验证用户是否有权限(仅ADMIN)
        permissionUtil.verifyCompanyAdmin(team.getCompanyId(), userEmail);
        
        teamRepository.delete(team);
    }
    
    /**
     * 转换为响应DTO
     */
    private TeamResponseDTO toResponseDTO(Team team, String companyName) {
        TeamResponseDTO dto = new TeamResponseDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLogo(team.getLogo());
        dto.setDescription(team.getDescription());
        dto.setCompanyId(team.getCompanyId());
        dto.setCompanyName(companyName);
        dto.setCreatorEmail(team.getCreatorEmail());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());
        return dto;
    }
}
