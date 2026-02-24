package com.example.houserentalsystem.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TagVO {
    private Long id;
    private String tagName;
    private String tagCode;
    private String category;
    private String icon;
    private Integer sort;
    private LocalDateTime createTime;
}