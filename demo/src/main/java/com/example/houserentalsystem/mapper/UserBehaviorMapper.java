package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {
    
    // 记录用户行为
    int insert(UserBehavior behavior);
    
    // 查询用户的行为历史
    List<UserBehavior> findByUserId(@Param("userId") Long userId);
    
    // 检查用户是否收藏过某房源
    int checkFavorite(@Param("userId") Long userId, @Param("houseId") Long houseId);
    
    // 取消收藏（删除行为记录）
    int deleteFavorite(@Param("userId") Long userId, @Param("houseId") Long houseId);
    
    // 统计用户的各种行为次数
    Map<String, Object> countUserBehaviors(@Param("userId") Long userId);
    
    // 获取用户最常浏览的房源标签（用于画像）
    List<Map<String, Object>> getUserPreferredTags(@Param("userId") Long userId);
}