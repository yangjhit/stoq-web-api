package com.stoq.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 配置BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置Spring Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // 公开端点 - 不需要认证
                .antMatchers("/users/send-verification-code").permitAll()
                .antMatchers("/users/send-reset-code").permitAll()
                .antMatchers("/users/get-verification-code").permitAll()
                .antMatchers("/users/register").permitAll()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/reset-password").permitAll()
                .antMatchers("/users/refresh-token").permitAll()
                // Swagger和H2控制台
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                // 集群管理端点需要认证
                .antMatchers("/clusters/**").authenticated()
                // 仓库管理端点需要认证
                .antMatchers("/stoqs/**").authenticated()
                // 团队管理端点需要认证
                .antMatchers("/teams/**").authenticated()
                // 集群成员管理端点需要认证
                .antMatchers("/cluster-members/**").authenticated()
                // 团队成员管理端点需要认证
                .antMatchers("/team-members/**").authenticated()
                // 商品分类管理端点需要认证
                .antMatchers("/product-categories/**").authenticated()
                // 商品模板管理端点需要认证
                .antMatchers("/product-templates/**").authenticated()
                // 其他所有端点需要认证
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // 允许H2控制台使用iframe
        http.headers().frameOptions().disable();
        
        return http.build();
    }
}
