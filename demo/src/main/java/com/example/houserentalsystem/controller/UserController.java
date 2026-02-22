package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.dto.RegisterDTO;
import com.example.houserentalsystem.entity.User;
import com.example.houserentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestParam String username, 
                              @RequestParam String password) {
        User user = userService.login(username, password);
        user.setPassword(null);
        return Result.success("登录成功", user);
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
}