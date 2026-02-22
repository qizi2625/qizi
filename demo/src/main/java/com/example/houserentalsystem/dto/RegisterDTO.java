package com.example.houserentalsystem.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String phone;
    private String email;
    private String realName;
    private String idCard;
    private Long roleId;  // 不传的话默认租客
}