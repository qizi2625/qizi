package com.example.houserentalsystem.service.impl;

import com.example.houserentalsystem.common.BusinessException;
import com.example.houserentalsystem.dto.BehaviorDTO;
import com.example.houserentalsystem.entity.*;
import com.example.houserentalsystem.mapper.*;
import com.example.houserentalsystem.service.BehaviorService;
import com.example.houserentalsystem.vo.TagVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // 行为类型对应的权重
    private static final Map<Integer, Float> BEHAVIOR_WEIGHT = Map.of(
            1, 1.0f,  // 浏览
            2, 3.0f,  // 收藏
            3, 2.0f,  // 咨询
            4, 1.5f   // 分享
    );

    @Override
    @Transactional
    public void recordBehavior(Long userId, BehaviorDTO behaviorDTO) {
        // 检查房源是否存在
        if (houseMapper.findById(behaviorDTO.getHouseId()) == null) {
            throw new BusinessException("房源不存在");
        }

        // 创建行为记录
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setHouseId(behaviorDTO.getHouseId());
        behavior.setBehaviorType(behaviorDTO.getBehaviorType());
        behavior.setWeight(BEHAVIOR_WEIGHT.getOrDefault(behaviorDTO.getBehaviorType(), 1.0f));

        userBehaviorMapper.insert(behavior);

        // 如果是收藏行为，不需要触发画像更新（避免频繁更新）
        // 可以在定时任务中批量更新画像
    }

    @Override
    @Transactional
    public void favoriteHouse(Long userId, Long houseId) {
        // 检查是否已经收藏
        if (checkFavorite(userId, houseId)) {
            throw new BusinessException("已经收藏过该房源");
        }

        BehaviorDTO behaviorDTO = new BehaviorDTO();
        behaviorDTO.setHouseId(houseId);
        behaviorDTO.setBehaviorType(2); // 收藏
        recordBehavior(userId, behaviorDTO);
    }

    @Override
    @Transactional
    public void unfavoriteHouse(Long userId, Long houseId) {
        userBehaviorMapper.deleteFavorite(userId, houseId);
    }

    @Override
    public boolean checkFavorite(Long userId, Long houseId) {
        return userBehaviorMapper.checkFavorite(userId, houseId) > 0;
    }

    @Override
    public Map<String, Object> getUserBehaviorStats(Long userId) {
        return userBehaviorMapper.countUserBehaviors(userId);
    }

    @Override
    @Transactional
    public void buildUserProfile(Long userId) {
        // 1. 获取用户偏好标签
        List<Map<String, Object>> preferredTagsData = userBehaviorMapper.getUserPreferredTags(userId);
        
        // 2. 转换为标签权重Map
        Map<String, Double> tagWeights = new HashMap<>();
        for (Map<String, Object> data : preferredTagsData) {
            String tagName = (String) data.get("tag_name");
            Number totalWeight = (Number) data.get("total_weight");
            tagWeights.put(tagName, totalWeight.doubleValue());
        }

        // 3. 获取用户浏览过的房源价格范围
        // 这里简化处理，实际可以从行为记录中统计
        BigDecimal priceMin = null;
        BigDecimal priceMax = null;

        // 4. 获取用户偏好的区域
        List<String> preferredDistricts = new ArrayList<>();

        // 5. 保存或更新用户画像
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
        }

        try {
            profile.setPreferredTags(objectMapper.writeValueAsString(tagWeights));
            profile.setPriceMin(priceMin);
            profile.setPriceMax(priceMax);
            profile.setPreferredDistricts(objectMapper.writeValueAsString(preferredDistricts));
            
            userProfileMapper.insertOrUpdate(profile);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON处理失败", e);
        }
    }

    @Override
    public List<TagVO> getUserPreferredTags(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null || profile.getPreferredTags() == null) {
            return new ArrayList<>();
        }

        try {
            Map<String, Double> tagWeights = objectMapper.readValue(
                    profile.getPreferredTags(), 
                    new TypeReference<Map<String, Double>>() {}
            );
            
            // 按权重排序，返回前10个标签
            List<TagVO> result = new ArrayList<>();
            tagWeights.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> {
                        TagVO vo = new TagVO();
                        vo.setTagName(entry.getKey());
                        // 可以设置其他属性
                        result.add(vo);
                    });
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析标签数据失败", e);
        }
    }

    @Override
    public Map<String, Object> getUserPricePreference(Long userId) {
        UserProfile profile = userProfileMapper.findByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        if (profile != null) {
            result.put("priceMin", profile.getPriceMin());
            result.put("priceMax", profile.getPriceMax());
        }
        return result;
    }
}