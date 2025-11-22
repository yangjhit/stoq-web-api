package com.stoq.service;
import com.stoq.dto.ClusterMemberDTO;
import com.stoq.entity.Cluster;
import com.stoq.entity.ClusterMember;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ClusterMemberRepository;
import com.stoq.repository.ClusterRepository;
import com.stoq.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClusterMemberService {
    
    private final ClusterMemberRepository clusterMemberRepository;
    private final ClusterRepository clusterRepository;
    private final PermissionUtil permissionUtil;
    
    /**
     * 添加集群成员(需要ADMIN权限)
     */
    @Transactional
    public ClusterMemberDTO addMember(ClusterMemberDTO dto, String operatorEmail) {
        // 验证集群是否存在
        Cluster cluster = clusterRepository.findById(dto.getClusterId())
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found: " + dto.getClusterId()));
        
        // 验证操作者是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(dto.getClusterId(), operatorEmail);
        
        // 检查成员是否已存在
        if (clusterMemberRepository.findByClusterIdAndUserEmail(dto.getClusterId(), dto.getUserEmail()).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this cluster");
        }
        
        // 创建成员
        ClusterMember member = new ClusterMember();
        member.setClusterId(dto.getClusterId());
        member.setUserEmail(dto.getUserEmail());
        member.setRole(dto.getRole());
        
        ClusterMember savedMember = clusterMemberRepository.save(member);
        return toDTO(savedMember);
    }
    
    /**
     * 获取集群的所有成员
     */
    public List<ClusterMemberDTO> getClusterMembers(Long clusterId, String userEmail) {
        // 验证集群是否存在
        clusterRepository.findById(clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found: " + clusterId));
        
        // 验证用户是否是集群成员
        permissionUtil.verifyClusterMember(clusterId, userEmail);
        
        List<ClusterMember> members = clusterMemberRepository.findByClusterId(clusterId);
        return members.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新成员角色(需要ADMIN权限)
     */
    @Transactional
    public ClusterMemberDTO updateMemberRole(Long memberId, String newRole, String operatorEmail) {
        ClusterMember member = clusterMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        
        // 验证操作者是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(member.getClusterId(), operatorEmail);
        
        // 不允许修改最后一个ADMIN
        if ("ADMIN".equals(member.getRole()) && !"ADMIN".equals(newRole)) {
            long adminCount = clusterMemberRepository.findByClusterIdAndRole(member.getClusterId(), "ADMIN").size();
            if (adminCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the last admin from the cluster");
            }
        }
        
        member.setRole(newRole);
        ClusterMember updatedMember = clusterMemberRepository.save(member);
        return toDTO(updatedMember);
    }
    
    /**
     * 删除集群成员(需要ADMIN权限)
     */
    @Transactional
    public void removeMember(Long memberId, String operatorEmail) {
        ClusterMember member = clusterMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        
        // 验证操作者是否有权限(仅ADMIN)
        permissionUtil.verifyClusterAdmin(member.getClusterId(), operatorEmail);
        
        // 不允许删除最后一个ADMIN
        if ("ADMIN".equals(member.getRole())) {
            long adminCount = clusterMemberRepository.findByClusterIdAndRole(member.getClusterId(), "ADMIN").size();
            if (adminCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the last admin from the cluster");
            }
        }
        
        clusterMemberRepository.delete(member);
    }
    
    /**
     * 转换为DTO
     */
    private ClusterMemberDTO toDTO(ClusterMember member) {
        ClusterMemberDTO dto = new ClusterMemberDTO();
        dto.setId(member.getId());
        dto.setClusterId(member.getClusterId());
        dto.setUserEmail(member.getUserEmail());
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());
        dto.setUpdatedAt(member.getUpdatedAt());
        return dto;
    }
}
