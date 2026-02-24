package com.example.houserentalsystem.service;

import com.example.houserentalsystem.dto.TagDTO;
import com.example.houserentalsystem.vo.TagVO;

import java.util.List;

public interface TagService {
    
    // 创建标签
    TagVO createTag(TagDTO tagDTO);
    
    // 更新标签
    TagVO updateTag(Long id, TagDTO tagDTO);
    
    // 删除标签
    void deleteTag(Long id);
    
    // 查询单个标签
    TagVO getTag(Long id);
    
    // 查询所有标签
    List<TagVO> getAllTags();
    
    // 按分类查询
    List<TagVO> getTagsByCategory(String category);
    
    // 给房源添加标签
    void addTagsToHouse(Long houseId, List<Long> tagIds);
    
    // 移除房源的标签
    void removeTagFromHouse(Long houseId, Long tagId);
    
    // 获取房源的标签
    List<TagVO> getHouseTags(Long houseId);
}