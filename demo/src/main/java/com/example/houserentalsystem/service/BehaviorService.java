package com.example.houserentalsystem.service;

import com.example.houserentalsystem.dto.BehaviorDTO;
import com.example.houserentalsystem.vo.TagVO;

import java.util.List;
import java.util.Map;

public interface BehaviorService {
    
    // 记录用户行为
    void recordBehavior(Long userId, BehaviorDTO behaviorDTO);
    
    // 收藏房源
    void favoriteHouse(Long userId, Long houseId);
    
    // 取消收藏
    void unfavoriteHouse(Long userId, Long houseId);
    
    // 检查是否已收藏
    boolean checkFavorite(Long userId, Long houseId);
    
    // 获取用户行为统计
    Map<String, Object> getUserBehaviorStats(Long userId);
    
    // 构建用户画像
    void buildUserProfile(Long userId);
    
    // 获取用户偏好标签
    List<TagVO> getUserPreferredTags(Long userId);
    
    // 获取用户偏好价格区间
    Map<String, Object> getUserPricePreference(Long userId);
}