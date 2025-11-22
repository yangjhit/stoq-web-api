package com.stoq.service;
import com.stoq.dto.ClusterResponseDTO;
import com.stoq.dto.CreateClusterDTO;
import com.stoq.entity.Cluster;
import com.stoq.entity.ClusterMember;
import com.stoq.enums.ClusterType;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ClusterMemberRepository;
import com.stoq.repository.ClusterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClusterService {
    
    private final ClusterRepository clusterRepository;
    private final ClusterMemberRepository clusterMemberRepository;
    
    /**
     * 创建集群
     */
    @Transactional
    public ClusterResponseDTO createCluster(CreateClusterDTO dto, String ownerEmail) {
        // 验证PROFESSIONAL类型必须提供注册号
        if ("PROFESSIONAL".equals(dto.getType())) {
            if (dto.getRegistrationNumber() == null || dto.getRegistrationNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required for PROFESSIONAL cluster type");
            }
        }
        
        // 验证集群类型
        ClusterType.fromCode(dto.getType());
        
        // 创建集群实体
        Cluster cluster = new Cluster();
        cluster.setName(dto.getName());
        cluster.setLogo(dto.getLogo());
        cluster.setAddress(dto.getAddress());
        cluster.setCountryId(dto.getCountryId());
        cluster.setCity(dto.getCity());
        cluster.setField(dto.getField());
        cluster.setEmployeeCount(dto.getEmployeeCount());
        cluster.setType(dto.getType());
        cluster.setRegistrationNumber(dto.getRegistrationNumber());
        cluster.setOwnerEmail(ownerEmail);
        
        // 保存集群
        Cluster savedCluster = clusterRepository.save(cluster);
        
        // 将创建者添加为ADMIN
        ClusterMember adminMember = new ClusterMember();
        adminMember.setClusterId(savedCluster.getId());
        adminMember.setUserEmail(ownerEmail);
        adminMember.setRole("ADMIN");
        clusterMemberRepository.save(adminMember);
        
        return toResponseDTO(savedCluster);
    }
    
    // 获取当前用户的所有集群
    public List<ClusterResponseDTO> getMyClusters(String ownerEmail) {
        List<Cluster> clusters = clusterRepository.findByOwnerEmail(ownerEmail);
        return clusters.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    // 根据ID获取集群(仅限创建者)
    public ClusterResponseDTO getClusterById(Long id, String ownerEmail) {
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found or you don't have permission: " + id));
        return toResponseDTO(cluster);
    }
    
    // 更新集群信息
    @Transactional
    public ClusterResponseDTO updateCluster(Long id, CreateClusterDTO dto, String ownerEmail) {
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found or you don't have permission: " + id));
        
        // 验证PROFESSIONAL类型必须提供注册号
        if ("PROFESSIONAL".equals(dto.getType())) {
            if (dto.getRegistrationNumber() == null || dto.getRegistrationNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required for PROFESSIONAL cluster type");
            }
        }
        
        // 验证集群类型
        ClusterType.fromCode(dto.getType());
        
        // 更新集群信息
        cluster.setName(dto.getName());
        cluster.setLogo(dto.getLogo());
        cluster.setAddress(dto.getAddress());
        cluster.setCountryId(dto.getCountryId());
        cluster.setCity(dto.getCity());
        cluster.setField(dto.getField());
        cluster.setEmployeeCount(dto.getEmployeeCount());
        cluster.setType(dto.getType());
        cluster.setRegistrationNumber(dto.getRegistrationNumber());
        
        Cluster updatedCluster = clusterRepository.save(cluster);
        return toResponseDTO(updatedCluster);
    }
    
    // 删除集群
    @Transactional
    public void deleteCluster(Long id, String ownerEmail) {
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found or you don't have permission: " + id));
        
        // 先删除集群成员
        clusterMemberRepository.deleteByClusterId(id);
        
        // 再删除集群
        clusterRepository.delete(cluster);
    }
    
    // 获取所有集群(管理员功能)
    public List<ClusterResponseDTO> getAllClusters() {
        List<Cluster> clusters = clusterRepository.findAll();
        return clusters.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应DTO
     */
    private ClusterResponseDTO toResponseDTO(Cluster cluster) {
        ClusterResponseDTO dto = new ClusterResponseDTO();
        dto.setId(cluster.getId());
        dto.setName(cluster.getName());
        dto.setLogo(cluster.getLogo());
        dto.setAddress(cluster.getAddress());
        dto.setCountryId(cluster.getCountryId());
        dto.setCity(cluster.getCity());
        dto.setField(cluster.getField());
        dto.setEmployeeCount(cluster.getEmployeeCount());
        dto.setType(cluster.getType());
        dto.setRegistrationNumber(cluster.getRegistrationNumber());
        dto.setOwnerEmail(cluster.getOwnerEmail());
        dto.setCreatedAt(cluster.getCreatedAt());
        dto.setUpdatedAt(cluster.getUpdatedAt());
        return dto;
    }
}
