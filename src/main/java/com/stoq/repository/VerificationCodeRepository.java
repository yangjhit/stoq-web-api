package com.stoq.repository;

import com.stoq.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    // 根据邮箱、验证码和场景查找
    Optional<VerificationCode> findByEmailAndCodeAndScenario(String email, String code, String scenario);
    
    // 根据邮箱和场景查找最新的验证码
    Optional<VerificationCode> findFirstByEmailAndScenarioOrderByCreatedAtDesc(String email, String scenario);
    
    // 删除指定邮箱的所有验证码
    void deleteByEmail(String email);
    
    // 删除指定邮箱和场景的验证码
    void deleteByEmailAndScenario(String email, String scenario);
}
