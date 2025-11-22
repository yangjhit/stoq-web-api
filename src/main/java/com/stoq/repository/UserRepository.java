package com.stoq.repository;
import com.stoq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // email是主键,所以findById就是根据email查找
    // 但为了语义清晰,可以添加这个方法
    Optional<User> findByEmail(String email);
    
    // 检查email是否存在
    boolean existsByEmail(String email);
    
    // 根据手机号查找
    Optional<User> findByPhone(String phone);
}
