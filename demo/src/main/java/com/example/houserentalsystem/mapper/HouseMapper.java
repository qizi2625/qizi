package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.House;
import com.example.houserentalsystem.dto.HouseQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseMapper {
    
    // 插入房源
    int insert(House house);
    
    // 根据ID查询
    House findById(@Param("id") Long id);
    
    // 查询房东的房源列表
    List<House> findByLandlordId(@Param("landlordId") Long landlordId);
    
    // 分页查询房源（带条件）
    List<House> pageQuery(@Param("query") HouseQueryDTO query);
    
    // 查询总数（用于分页）
    Long countQuery(@Param("query") HouseQueryDTO query);
    
    // 更新房源
    int update(House house);
    
    // 删除房源
    int deleteById(@Param("id") Long id);
    
    // 更新房源状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    // 增加浏览次数
    int incrementViewCount(@Param("id") Long id);
    
    // 增加收藏次数
    int incrementFavoriteCount(@Param("id") Long id);
}