package com.example.houserentalsystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class House {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal area;
    private Integer bedroomCount;
    private Integer livingRoomCount;
    private Integer bathroomCount;
    private String floor;
    private String propertyType;
    private String decoration;
    private String orientation;
    private String province;
    private String city;
    private String district;
    private String address;
    private Long landlordId;
    private Integer status;  // 0-待审核，1-已上架，2-已下架，3-已出租
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}