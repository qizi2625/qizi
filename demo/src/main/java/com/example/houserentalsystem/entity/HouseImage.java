package com.example.houserentalsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HouseImage {
    private Long id;
    private Long houseId;
    private String imageUrl;
    private Integer sort;
    private Integer type;  // 1-封面图，2-普通图
    private LocalDateTime createTime;
}