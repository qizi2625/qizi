package com.example.houserentalsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserBehavior {
    private Long id;
    private Long userId;
    private Long houseId;
    private Integer behaviorType;  // 1-浏览，2-收藏，3-咨询，4-分享
    private Float weight;
    private LocalDateTime createTime;
}
