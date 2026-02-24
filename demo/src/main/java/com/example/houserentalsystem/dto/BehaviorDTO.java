package com.example.houserentalsystem.dto;

import lombok.Data;

@Data
public class BehaviorDTO {
    private Long houseId;
    private Integer behaviorType;  // 1-浏览，2-收藏，3-咨询，4-分享
}