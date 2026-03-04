package com.example.houserentalsystem.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RecommendHouseVO {
    private Long id;
    private String title;
    private BigDecimal price;
    private BigDecimal area;
    private String coverImage;
    private String district;
    private Integer bedroomCount;
    private Integer livingRoomCount;
    private Integer bathroomCount;
    private String decoration;
    private String orientation;
    private Double matchScore;  // 匹配度分数
    private List<TagVO> tags;    // 房源标签
    private Integer viewCount;
    private Integer favoriteCount;
}