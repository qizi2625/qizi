package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.dto.BehaviorDTO;
import com.example.houserentalsystem.service.BehaviorService;
import com.example.houserentalsystem.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/behavior")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    /**
     * 记录用户行为
     */
    @PostMapping("/record")
    @RequireToken
    public Result<String> recordBehavior(HttpServletRequest request, @RequestBody BehaviorDTO behaviorDTO) {
        Long userId = (Long) request.getAttribute("userId");
        behaviorService.recordBehavior(userId, behaviorDTO);
        return Result.success("记录成功");
    }

    /**
     * 收藏房源
     */
    @PostMapping("/favorite/{houseId}")
    @RequireToken
    public Result<String> favoriteHouse(HttpServletRequest request, @PathVariable Long houseId) {
        Long userId = (Long) request.getAttribute("userId");
        behaviorService.favoriteHouse(userId, houseId);
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite/{houseId}")
    @RequireToken
    public Result<String> unfavoriteHouse(HttpServletRequest request, @PathVariable Long houseId) {
        Long userId = (Long) request.getAttribute("userId");
        behaviorService.unfavoriteHouse(userId, houseId);
        return Result.success("取消收藏成功");
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/favorite/check/{houseId}")
    @RequireToken
    public Result<Boolean> checkFavorite(HttpServletRequest request, @PathVariable Long houseId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isFavorite = behaviorService.checkFavorite(userId, houseId);
        return Result.success(isFavorite);
    }

    /**
     * 获取用户行为统计
     */
    @GetMapping("/stats")
    @RequireToken
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> stats = behaviorService.getUserBehaviorStats(userId);
        return Result.success(stats);
    }

    /**
     * 手动触发构建用户画像
     */
    @PostMapping("/profile/build")
    @RequireToken
    public Result<String> buildProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        behaviorService.buildUserProfile(userId);
        return Result.success("画像构建成功");
    }

    /**
     * 获取用户偏好标签
     */
    @GetMapping("/profile/tags")
    @RequireToken
    public Result<List<TagVO>> getPreferredTags(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<TagVO> tags = behaviorService.getUserPreferredTags(userId);
        return Result.success(tags);
    }

    /**
     * 获取用户价格偏好
     */
    @GetMapping("/profile/price")
    @RequireToken
    public Result<Map<String, Object>> getPricePreference(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> pricePref = behaviorService.getUserPricePreference(userId);
        return Result.success(pricePref);
    }
}