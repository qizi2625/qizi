package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    
    User findByUsername(@Param("username") String username);
    
    User findByPhone(@Param("phone") String phone);
    
    User findById(@Param("id") Long id);
    
    int insert(User user);
}