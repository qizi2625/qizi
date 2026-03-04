package com.example.houserentalsystem.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RecommendContext {
    private Long userId;
    private Map<String, Double> userTagPreferences;  // 用户标签偏好
    private List<Long> viewedHouseIds;                // 已浏览过的房源
    private List<Long> favoriteHouseIds;              // 收藏的房源
    private Integer limit;                             // 推荐数量
}