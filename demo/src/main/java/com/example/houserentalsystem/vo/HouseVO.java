package com.example.houserentalsystem.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HouseVO {
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
    private String landlordName;  // 房东姓名
    private String landlordPhone;  // 房东电话
    private Integer status;
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
    private List<String> imageUrls;  // 图片列表
    private String coverImage;  // 封面图
    private List<TagVO> tags;  //标签
}