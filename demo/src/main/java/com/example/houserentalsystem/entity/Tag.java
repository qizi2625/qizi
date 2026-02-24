package com.example.houserentalsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tag {
    private Long id;
    private String tagName;
    private String tagCode;
    private String category;
    private String icon;
    private Integer sort;
    private LocalDateTime createTime;
}