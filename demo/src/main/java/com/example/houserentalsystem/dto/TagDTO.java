package com.example.houserentalsystem.dto;

import lombok.Data;

@Data
public class TagDTO {
    private String tagName;
    private String tagCode;
    private String category;
    private String icon;
    private Integer sort;
}