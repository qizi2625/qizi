package com.example.houserentalsystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserProfile {
    private Long id;
    private Long userId;
    private String preferredTags;  // JSON格式
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String preferredDistricts;  // JSON格式
    private LocalDateTime lastUpdateTime;
}