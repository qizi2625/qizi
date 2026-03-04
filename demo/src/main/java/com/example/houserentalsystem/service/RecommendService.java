package com.example.houserentalsystem.service;

import com.example.houserentalsystem.vo.RecommendHouseVO;

import java.util.List;

public interface RecommendService {

    /**
     * 获取用户的个性化推荐（猜你喜欢）
     */
    List<RecommendHouseVO> getPersonalizedRecommendations(Long userId, int size);

    /**
     * 获取相似房源推荐
     */
    List<RecommendHouseVO> getSimilarHouses(Long houseId, int size);

    /**
     * 获取热门房源推荐
     */
    List<RecommendHouseVO> getHotHouses(int size);

    /**
     * 获取最新房源推荐
     */
    List<RecommendHouseVO> getNewHouses(int size);

    /**
     * 获取今日精选（混合推荐）
     */
    List<RecommendHouseVO> getDailyPicks(Long userId, int size);

    /**
     * 刷新用户推荐缓存
     */
    void refreshUserRecommendations(Long userId);
}