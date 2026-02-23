package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.HouseImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseImageMapper {
    
    // 批量插入图片
    int insertBatch(@Param("list") List<HouseImage> images);
    
    // 根据房源ID查询图片
    List<HouseImage> findByHouseId(@Param("houseId") Long houseId);
    
    // 删除房源的所有图片
    int deleteByHouseId(@Param("houseId") Long houseId);
    
    // 获取房源封面图
    HouseImage findCoverByHouseId(@Param("houseId") Long houseId);
}