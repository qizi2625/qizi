package com.example.houserentalsystem.service.impl;

import com.example.houserentalsystem.entity.User;
import com.example.houserentalsystem.mapper.UserMapper;
import com.example.houserentalsystem.service.UserService;
import com.example.houserentalsystem.dto.RegisterDTO;
import com.example.houserentalsystem.utils.PasswordEncoderUtil;
import com.example.houserentalsystem.common.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoderUtil passwordEncoder;
    
    @Override
    public User register(RegisterDTO registerDTO) {
        // 1. 检查用户名
        User existingUser = userMapper.findByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 2. 检查手机号
        existingUser = userMapper.findByPhone(registerDTO.getPhone());
        if (existingUser != null) {
            throw new BusinessException("手机号已注册");
        }
        
        // 3. 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRealName(registerDTO.getRealName());
        user.setIdCard(registerDTO.getIdCard());
        user.setRoleId(registerDTO.getRoleId() != null ? registerDTO.getRoleId() : 3L); // 默认租客
        user.setStatus(1);
        
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        
        return user;
    }
    
    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }
}