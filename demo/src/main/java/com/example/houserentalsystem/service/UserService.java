package com.example.houserentalsystem.service;

import com.example.houserentalsystem.entity.User;
import com.example.houserentalsystem.dto.RegisterDTO;

public interface UserService {
    
    User register(RegisterDTO registerDTO);
    
    User login(String username, String password);
    
    User getById(Long id);
}