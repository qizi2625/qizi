package com.example.houserentalsystem.utils;

import com.example.houserentalsystem.entity.Tag;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode; 

@Component
public class RecommendationAlgorithm {

    /**
     * 计算用户与房源的匹配度（基于标签）
     */
    public double calculateMatchScore(Map<String, Double> userTagPreferences, 
                                      List<Tag> houseTags,
                                      BigDecimal userPriceMin,
                                      BigDecimal userPriceMax,
                                      BigDecimal housePrice) {
        double score = 0.0;
        
        // 1. 标签匹配度（权重 0.6）
        if (userTagPreferences != null && !userTagPreferences.isEmpty() && houseTags != null) {
            double tagScore = 0.0;
            double totalWeight = 0.0;
            
            for (Tag tag : houseTags) {
                Double preference = userTagPreferences.get(tag.getTagName());
                if (preference != null) {
                    tagScore += preference;
                    totalWeight += 1.0;
                }
            }
            
            if (totalWeight > 0) {
                tagScore = tagScore / totalWeight;  // 归一化
                score += tagScore * 0.6;
            }
        }
        
        // 2. 价格匹配度（权重 0.3）
        if (userPriceMin != null && userPriceMax != null && housePrice != null) {
            double priceScore = calculatePriceMatchScore(userPriceMin, userPriceMax, housePrice);
            score += priceScore * 0.3;
        }
        
        // 3. 基础热度加成（权重 0.1）
        // 这部分在调用时动态计算
        
        return score;
    }

    /**
     * 计算价格匹配度
     */
    private double calculatePriceMatchScore(BigDecimal userMin, BigDecimal userMax, BigDecimal housePrice) {
        // 如果房源价格在用户偏好范围内，得1分
        if (housePrice.compareTo(userMin) >= 0 && housePrice.compareTo(userMax) <= 0) {
            return 1.0;
        }
        
        // 计算距离偏好区间的远近
        BigDecimal diff;
        if (housePrice.compareTo(userMin) < 0) {
            diff = userMin.subtract(housePrice);
        } else {
            diff = housePrice.subtract(userMax);
        }
        
        // 价格范围宽度
        BigDecimal range = userMax.subtract(userMin);
        if (range.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        // 距离越远分数越低
        double ratio = diff.divide(range, 2, RoundingMode.HALF_UP).doubleValue();
        return Math.max(0, 1.0 - ratio);
    }

    /**
     * 计算房源之间的相似度（基于标签）
     */
    public double calculateHouseSimilarity(List<Tag> tags1, List<Tag> tags2) {
        if (tags1 == null || tags2 == null || tags1.isEmpty() || tags2.isEmpty()) {
            return 0.0;
        }
        
        Set<Long> tagIds1 = tags1.stream().map(Tag::getId).collect(Collectors.toSet());
        Set<Long> tagIds2 = tags2.stream().map(Tag::getId).collect(Collectors.toSet());
        
        // 计算交集大小
        Set<Long> intersection = new HashSet<>(tagIds1);
        intersection.retainAll(tagIds2);
        
        // 计算并集大小
        Set<Long> union = new HashSet<>(tagIds1);
        union.addAll(tagIds2);
        
        // Jaccard相似度
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * 排序推荐结果
     */
    public <T> List<T> sortRecommendations(List<T> items, Map<T, Double> scores, int limit) {
        return items.stream()
                .sorted((a, b) -> Double.compare(scores.getOrDefault(b, 0.0), scores.getOrDefault(a, 0.0)))
                .limit(limit)
                .collect(Collectors.toList());
    }
}