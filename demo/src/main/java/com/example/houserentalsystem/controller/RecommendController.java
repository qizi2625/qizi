package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.service.RecommendService;
import com.example.houserentalsystem.vo.RecommendHouseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取个性化推荐（猜你喜欢）
     */
    @GetMapping("/personal")
    @RequireToken
    public Result<List<RecommendHouseVO>> getPersonalizedRecommendations(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        List<RecommendHouseVO> recommendations = recommendService.getPersonalizedRecommendations(userId, size);
        return Result.success(recommendations);
    }

    /**
     * 获取相似房源推荐
     */
    @GetMapping("/similar/{houseId}")
    public Result<List<RecommendHouseVO>> getSimilarHouses(
            @PathVariable Long houseId,
            @RequestParam(defaultValue = "6") int size) {
        List<RecommendHouseVO> recommendations = recommendService.getSimilarHouses(houseId, size);
        return Result.success(recommendations);
    }

    /**
     * 获取热门房源推荐
     */
    @GetMapping("/hot")
    public Result<List<RecommendHouseVO>> getHotHouses(
            @RequestParam(defaultValue = "10") int size) {
        List<RecommendHouseVO> recommendations = recommendService.getHotHouses(size);
        return Result.success(recommendations);
    }

    /**
     * 获取最新房源推荐
     */
    @GetMapping("/new")
    public Result<List<RecommendHouseVO>> getNewHouses(
            @RequestParam(defaultValue = "10") int size) {
        List<RecommendHouseVO> recommendations = recommendService.getNewHouses(size);
        return Result.success(recommendations);
    }

    /**
     * 获取今日精选（混合推荐）
     */
    @GetMapping("/daily")
    @RequireToken
    public Result<List<RecommendHouseVO>> getDailyPicks(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        List<RecommendHouseVO> recommendations = recommendService.getDailyPicks(userId, size);
        return Result.success(recommendations);
    }

    /**
     * 刷新用户推荐缓存
     */
    @PostMapping("/refresh")
    @RequireToken
    public Result<String> refreshRecommendations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        recommendService.refreshUserRecommendations(userId);
        return Result.success("推荐缓存已刷新");
    }
}