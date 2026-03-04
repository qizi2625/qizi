package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    
    User findByUsername(@Param("username") String username);
    
    User findByPhone(@Param("phone") String phone);
    
    User findById(@Param("id") Long id);
    
    int insert(User user);

    List<Long> findActiveUserIds(@Param("days") int days);
}