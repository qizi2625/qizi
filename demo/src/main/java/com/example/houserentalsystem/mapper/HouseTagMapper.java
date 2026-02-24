package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.HouseTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseTagMapper {
    
    // 批量添加房源标签
    int insertBatch(@Param("list") List<HouseTag> houseTags);
    
    // 删除房源的所有标签
    int deleteByHouseId(@Param("houseId") Long houseId);
    
    // 删除指定的房源标签
    int deleteByHouseAndTag(@Param("houseId") Long houseId, @Param("tagId") Long tagId);
    
    // 查询房源的标签ID列表
    List<Long> findTagIdsByHouseId(@Param("houseId") Long houseId);
}