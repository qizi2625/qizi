package com.example.houserentalsystem.task;

import com.example.houserentalsystem.mapper.UserMapper;
import com.example.houserentalsystem.service.BehaviorService;
import com.example.houserentalsystem.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class RecommendScheduledTask {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BehaviorService behaviorService;

    @Autowired
    private RecommendService recommendService;

    /**
     * 每天凌晨2点更新所有活跃用户的画像
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateUserProfiles() {
        // 获取最近7天有行为的用户
        List<Long> activeUserIds = userMapper.findActiveUserIds(7);
        
        for (Long userId : activeUserIds) {
            try {
                // 重新构建用户画像
                behaviorService.buildUserProfile(userId);
                // 刷新推荐缓存
                recommendService.refreshUserRecommendations(userId);
            } catch (Exception e) {
                // 记录日志，继续处理下一个用户
                System.err.println("更新用户画像失败: userId=" + userId);
            }
        }
    }

    /**
     * 每小时更新热门房源缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshHotHouses() {
        // 热门房源缓存会在下次访问时自动刷新
        // 这里可以预热热门房源
    }
}