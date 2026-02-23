package com.example.houserentalsystem.dto;

import com.example.houserentalsystem.entity.User;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private User user;

    public LoginResponseDTO(String token, User user) {
        this.token = token;
        this.user = user;
        this.user.setPassword(null); // 清除敏感信息
    }
}