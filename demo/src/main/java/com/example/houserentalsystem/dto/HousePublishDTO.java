package com.example.houserentalsystem.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HousePublishDTO {
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
    private List<String> imageUrls;  // 图片URL列表
}
