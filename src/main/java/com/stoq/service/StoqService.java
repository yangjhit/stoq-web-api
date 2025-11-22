package com.stoq.service;
import com.stoq.dto.CreateStoqDTO;
import com.stoq.dto.StoqResponseDTO;
import com.stoq.entity.Cluster;
import com.stoq.entity.Stoq;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ClusterRepository;
import com.stoq.repository.StoqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoqService {
    
    private final StoqRepository stoqRepository;
    private final ClusterRepository clusterRepository;
    
    /**
     * 创建仓库
     */
    @Transactional
    public StoqResponseDTO createStoq(CreateStoqDTO dto, String creatorEmail) {
        // 验证集群是否存在且属于当前用户
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(dto.getClusterId(), creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cluster not found or you don't have permission: " + dto.getClusterId()));
        
        // 创建仓库实体
        Stoq stoq = new Stoq();
        stoq.setName(dto.getName());
        stoq.setDescription(dto.getDescription());
        stoq.setAdministrator(dto.getAdministrator());
        stoq.setClusterId(dto.getClusterId());
        stoq.setCreatorEmail(creatorEmail);
        
        // 保存仓库
        Stoq savedStoq = stoqRepository.save(stoq);
        
        return toResponseDTO(savedStoq, cluster.getName());
    }
    
    /**
     * 获取指定集群的所有仓库
     */
    public List<StoqResponseDTO> getStoqsByCluster(Long clusterId, String userEmail) {
        // 验证集群是否存在且属于当前用户
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(clusterId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cluster not found or you don't have permission: " + clusterId));
        
        List<Stoq> stoqs = stoqRepository.findByClusterId(clusterId);
        return stoqs.stream()
                .map(stoq -> toResponseDTO(stoq, cluster.getName()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取当前用户创建的所有仓库
     */
    public List<StoqResponseDTO> getMyStoqs(String creatorEmail) {
        List<Stoq> stoqs = stoqRepository.findByCreatorEmail(creatorEmail);
        return stoqs.stream()
                .map(stoq -> {
                    String clusterName = clusterRepository.findById(stoq.getClusterId())
                            .map(Cluster::getName)
                            .orElse("Unknown");
                    return toResponseDTO(stoq, clusterName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取仓库
     */
    public StoqResponseDTO getStoqById(Long id, String userEmail) {
        Stoq stoq = stoqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stoq not found: " + id));
        
        // 验证用户是否有权限访问(必须是集群所有者)
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(stoq.getClusterId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You don't have permission to access this stoq"));
        
        return toResponseDTO(stoq, cluster.getName());
    }
    
    /**
     * 更新仓库信息
     */
    @Transactional
    public StoqResponseDTO updateStoq(Long id, CreateStoqDTO dto, String userEmail) {
        Stoq stoq = stoqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stoq not found: " + id));
        
        // 验证用户是否有权限(必须是集群所有者)
        Cluster cluster = clusterRepository.findByIdAndOwnerEmail(stoq.getClusterId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You don't have permission to update this stoq"));
        
        // 如果要更改集群,验证新集群是否属于当前用户
        if (!stoq.getClusterId().equals(dto.getClusterId())) {
            Cluster newCluster = clusterRepository.findByIdAndOwnerEmail(dto.getClusterId(), userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "New cluster not found or you don't have permission: " + dto.getClusterId()));
            cluster = newCluster;
        }
        
        // 更新仓库信息
        stoq.setName(dto.getName());
        stoq.setDescription(dto.getDescription());
        stoq.setAdministrator(dto.getAdministrator());
        stoq.setClusterId(dto.getClusterId());
        
        Stoq updatedStoq = stoqRepository.save(stoq);
        return toResponseDTO(updatedStoq, cluster.getName());
    }
    
    /**
     * 删除仓库
     */
    @Transactional
    public void deleteStoq(Long id, String userEmail) {
        Stoq stoq = stoqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stoq not found: " + id));
        
        // 验证用户是否有权限(必须是集群所有者)
        clusterRepository.findByIdAndOwnerEmail(stoq.getClusterId(), userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You don't have permission to delete this stoq"));
        
        stoqRepository.delete(stoq);
    }
    
    /**
     * 转换为响应DTO
     */
    private StoqResponseDTO toResponseDTO(Stoq stoq, String clusterName) {
        StoqResponseDTO dto = new StoqResponseDTO();
        dto.setId(stoq.getId());
        dto.setName(stoq.getName());
        dto.setDescription(stoq.getDescription());
        dto.setAdministrator(stoq.getAdministrator());
        dto.setClusterId(stoq.getClusterId());
        dto.setClusterName(clusterName);
        dto.setCreatorEmail(stoq.getCreatorEmail());
        dto.setCreatedAt(stoq.getCreatedAt());
        dto.setUpdatedAt(stoq.getUpdatedAt());
        return dto;
    }
}
