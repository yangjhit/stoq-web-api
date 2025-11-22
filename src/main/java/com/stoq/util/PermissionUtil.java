package com.stoq.util;
import com.stoq.entity.Cluster;
import com.stoq.entity.ClusterMember;
import com.stoq.exception.ResourceNotFoundException;
import com.stoq.repository.ClusterMemberRepository;
import com.stoq.repository.ClusterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 权限检查工具类
 */
@Component
@RequiredArgsConstructor
public class PermissionUtil {
    
    private final ClusterRepository clusterRepository;
    private final ClusterMemberRepository clusterMemberRepository;
    
    /**
     * 检查用户是否是集群的ADMIN
     */
    public boolean isClusterAdmin(Long clusterId, String userEmail) {
        return clusterMemberRepository.findByClusterIdAndUserEmail(clusterId, userEmail)
                .map(member -> "ADMIN".equals(member.getRole()))
                .orElse(false);
    }
    
    /**
     * 检查用户是否是集群的ADMIN或MEMBER
     */
    public boolean isClusterAdminOrMember(Long clusterId, String userEmail) {
        return clusterMemberRepository.findByClusterIdAndUserEmail(clusterId, userEmail)
                .isPresent();
    }
    
    /**
     * 检查用户是否是集群成员
     */
    public boolean isClusterMember(Long clusterId, String userEmail) {
        return clusterMemberRepository.findByClusterIdAndUserEmail(clusterId, userEmail)
                .isPresent();
    }
    
    /**
     * 获取用户在集群中的角色
     */
    public String getUserRoleInCluster(Long clusterId, String userEmail) {
        return clusterMemberRepository.findByClusterIdAndUserEmail(clusterId, userEmail)
                .map(ClusterMember::getRole)
                .orElse(null);
    }
    
    /**
     * 验证用户是否有权限访问集群(必须是ADMIN)
     */
    public void verifyClusterAdmin(Long clusterId, String userEmail) {
        if (!isClusterAdmin(clusterId, userEmail)) {
            throw new ResourceNotFoundException("You don't have permission to access this cluster");
        }
    }
    
    /**
     * 验证用户是否有权限访问集群(ADMIN或MEMBER)
     */
    public void verifyClusterAdminOrMember(Long clusterId, String userEmail) {
        if (!isClusterAdminOrMember(clusterId, userEmail)) {
            throw new ResourceNotFoundException("You don't have permission to access this cluster");
        }
    }
    
    /**
     * 验证用户是否是集群成员
     */
    public void verifyClusterMember(Long clusterId, String userEmail) {
        if (!isClusterMember(clusterId, userEmail)) {
            throw new ResourceNotFoundException("You are not a member of this cluster");
        }
    }
}
