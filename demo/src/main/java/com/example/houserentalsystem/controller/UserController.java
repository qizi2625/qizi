package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.dto.LoginResponseDTO;
import com.example.houserentalsystem.dto.RegisterDTO;
import com.example.houserentalsystem.entity.User;
import com.example.houserentalsystem.service.UserService;
import com.example.houserentalsystem.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;  // 注入 JWT 工具类

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@RequestParam String username, 
                                          @RequestParam String password) {
        // 1. 用户登录验证
        User user = userService.login(username, password);
        
        // 2. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId());
        
        // 3. 返回 token 和用户信息
        LoginResponseDTO response = new LoginResponseDTO(token, user);
        return Result.success("登录成功", response);
    }

    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 获取当前登录用户信息（需要token）
     */
    @GetMapping("/current")
    @RequireToken  // 需要token才能访问
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
}