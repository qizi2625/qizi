package com.example.houserentalsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码加密器（供 PasswordEncoderUtil 使用）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤链
     * 因为我们要用 JWT 做自定义登录，所以暂时放行所有请求
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF（跨站请求伪造防护），开发阶段先关闭
            .csrf(csrf -> csrf.disable())
            
            // 2. 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 所有请求都允许访问（暂时放行）
                .anyRequest().permitAll()
            )
            
            // 3. 禁用默认的表单登录（我们不想要 Spring 的登录页）
            .formLogin(form -> form.disable())
            
            // 4. 禁用默认的 HTTP Basic 认证
            .httpBasic(httpBasic -> httpBasic.disable());
        
        return http.build();
    }
}