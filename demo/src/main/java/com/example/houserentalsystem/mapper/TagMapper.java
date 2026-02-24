package com.example.houserentalsystem.mapper;

import com.example.houserentalsystem.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    
    // 插入标签
    int insert(Tag tag);
    
    // 根据ID查询
    Tag findById(@Param("id") Long id);
    
    // 查询所有标签
    List<Tag> findAll();
    
    // 按分类查询
    List<Tag> findByCategory(@Param("category") String category);
    
    // 更新标签
    int update(Tag tag);
    
    // 删除标签
    int deleteById(@Param("id") Long id);
    
    // 根据房源ID查询标签列表
    List<Tag> findByHouseId(@Param("houseId") Long houseId);
}