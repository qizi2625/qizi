package com.example.houserentalsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HouseTag {
    private Long id;
    private Long houseId;
    private Long tagId;
    private LocalDateTime createTime;
}