package com.example.houserentalsystem.interceptor;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.BusinessException;
import com.example.houserentalsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 如果不是方法请求，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 2. 检查方法上是否有 @RequireToken 注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireToken requireToken = handlerMethod.getMethodAnnotation(RequireToken.class);
        
        // 3. 如果没有注解，或者注解的 required = false，放行
        if (requireToken == null || !requireToken.required()) {
            return true;
        }

        // 4. 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        // 5. 去掉 Bearer 前缀（如果有）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 6. 验证 token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // 7. 将用户信息存入 request（供后续使用）
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        
        return true;
    }
}