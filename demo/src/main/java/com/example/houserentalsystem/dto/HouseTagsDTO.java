package com.example.houserentalsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class HouseTagsDTO {
    private Long houseId;
    private List<Long> tagIds;
}