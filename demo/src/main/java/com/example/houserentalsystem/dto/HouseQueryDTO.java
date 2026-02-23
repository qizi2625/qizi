package com.example.houserentalsystem.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HouseQueryDTO {
    private String keyword;      // 关键词搜索
    private String city;         // 城市
    private String district;     // 区域
    private BigDecimal minPrice; // 最低价格
    private BigDecimal maxPrice; // 最高价格
    private Integer bedroomCount;// 卧室数量
    private String orientation;  // 朝向
    private String decoration;   // 装修
    private Integer page = 1;     // 页码
    private Integer size = 10;    // 每页大小
    private String sortBy = "create_time";  // 排序字段
    private String sortOrder = "desc";      // 排序方向

    public Integer getOffset() {
        return (page - 1) * size;
    }
}