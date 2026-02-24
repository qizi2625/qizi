package com.example.houserentalsystem.service.impl;

import com.example.houserentalsystem.common.BusinessException;
import com.example.houserentalsystem.dto.TagDTO;
import com.example.houserentalsystem.entity.HouseTag;
import com.example.houserentalsystem.entity.Tag;
import com.example.houserentalsystem.mapper.HouseMapper;
import com.example.houserentalsystem.mapper.HouseTagMapper;
import com.example.houserentalsystem.mapper.TagMapper;
import com.example.houserentalsystem.service.TagService;
import com.example.houserentalsystem.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private HouseTagMapper houseTagMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public TagVO createTag(TagDTO tagDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        
        tagMapper.insert(tag);
        return convertToVO(tag);
    }

    @Override
    public TagVO updateTag(Long id, TagDTO tagDTO) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        
        BeanUtils.copyProperties(tagDTO, tag);
        tag.setId(id);
        
        tagMapper.update(tag);
        return convertToVO(tag);
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        tagMapper.deleteById(id);
    }

    @Override
    public TagVO getTag(Long id) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return convertToVO(tag);
    }

    @Override
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagMapper.findAll();
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getTagsByCategory(String category) {
        List<Tag> tags = tagMapper.findByCategory(category);
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addTagsToHouse(Long houseId, List<Long> tagIds) {
        // 检查房源是否存在
        if (houseMapper.findById(houseId) == null) {
            throw new BusinessException("房源不存在");
        }
        
        // 先删除原有的标签关联
        houseTagMapper.deleteByHouseId(houseId);
        
        // 添加新的标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (Long tagId : tagIds) {
                // 检查标签是否存在
                if (tagMapper.findById(tagId) == null) {
                    throw new BusinessException("标签ID " + tagId + " 不存在");
                }
                HouseTag houseTag = new HouseTag();
                houseTag.setHouseId(houseId);
                houseTag.setTagId(tagId);
                houseTags.add(houseTag);
            }
            houseTagMapper.insertBatch(houseTags);
        }
    }

    @Override
    public void removeTagFromHouse(Long houseId, Long tagId) {
        houseTagMapper.deleteByHouseAndTag(houseId, tagId);
    }

    @Override
    public List<TagVO> getHouseTags(Long houseId) {
        List<Tag> tags = tagMapper.findByHouseId(houseId);
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private TagVO convertToVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}