package com.example.houserentalsystem.service.impl;

import com.example.houserentalsystem.common.BusinessException;
import com.example.houserentalsystem.dto.HousePublishDTO;
import com.example.houserentalsystem.dto.HouseQueryDTO;
import com.example.houserentalsystem.entity.House;
import com.example.houserentalsystem.entity.HouseImage;
import com.example.houserentalsystem.entity.User;
import com.example.houserentalsystem.mapper.HouseMapper;
import com.example.houserentalsystem.mapper.HouseImageMapper;
import com.example.houserentalsystem.mapper.UserMapper;
import com.example.houserentalsystem.service.HouseService;
import com.example.houserentalsystem.vo.HouseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseImageMapper houseImageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public HouseVO publish(Long landlordId, HousePublishDTO publishDTO) {
        // 1. 检查房东是否存在
        User landlord = userMapper.findById(landlordId);
        if (landlord == null || landlord.getRoleId() != 2) {
            throw new BusinessException("只有房东才能发布房源");
        }

        // 2. 创建房源
        House house = new House();
        BeanUtils.copyProperties(publishDTO, house);
        house.setLandlordId(landlordId);
        house.setStatus(0); // 待审核
        house.setViewCount(0);
        house.setFavoriteCount(0);

        houseMapper.insert(house);

        // 3. 保存图片
        if (publishDTO.getImageUrls() != null && !publishDTO.getImageUrls().isEmpty()) {
            List<HouseImage> images = new ArrayList<>();
            for (int i = 0; i < publishDTO.getImageUrls().size(); i++) {
                HouseImage image = new HouseImage();
                image.setHouseId(house.getId());
                image.setImageUrl(publishDTO.getImageUrls().get(i));
                image.setSort(i);
                image.setType(i == 0 ? 1 : 2); // 第一张设为封面
                images.add(image);
            }
            houseImageMapper.insertBatch(images);
        }

        return convertToVO(house);
    }

    @Override
    public HouseVO getHouseDetail(Long id) {
        // 1. 查询房源
        House house = houseMapper.findById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }

        // 2. 增加浏览次数
        houseMapper.incrementViewCount(id);

        return convertToVO(house);
    }

    @Override
    public Map<String, Object> pageQuery(HouseQueryDTO queryDTO) {
        List<House> houses = houseMapper.pageQuery(queryDTO);
        Long total = houseMapper.countQuery(queryDTO);

        List<HouseVO> voList = houses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", voList);
        result.put("total", total);
        result.put("page", queryDTO.getPage());
        result.put("size", queryDTO.getSize());
        result.put("pages", (total + queryDTO.getSize() - 1) / queryDTO.getSize());

        return result;
    }

    @Override
    public List<HouseVO> getLandlordHouses(Long landlordId) {
        List<House> houses = houseMapper.findByLandlordId(landlordId);
        return houses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HouseVO updateHouse(Long id, Long landlordId, HousePublishDTO updateDTO) {
        // 1. 检查房源是否存在且属于该房东
        House house = houseMapper.findById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException("无权修改此房源");
        }

        // 2. 更新房源信息
        BeanUtils.copyProperties(updateDTO, house);
        house.setId(id);
        houseMapper.update(house);

        // 3. 更新图片（先删除旧图，再添加新图）
        if (updateDTO.getImageUrls() != null && !updateDTO.getImageUrls().isEmpty()) {
            houseImageMapper.deleteByHouseId(id);
            
            List<HouseImage> images = new ArrayList<>();
            for (int i = 0; i < updateDTO.getImageUrls().size(); i++) {
                HouseImage image = new HouseImage();
                image.setHouseId(id);
                image.setImageUrl(updateDTO.getImageUrls().get(i));
                image.setSort(i);
                image.setType(i == 0 ? 1 : 2);
                images.add(image);
            }
            houseImageMapper.insertBatch(images);
        }

        return convertToVO(house);
    }

    @Override
    @Transactional
    public void deleteHouse(Long id, Long landlordId) {
        House house = houseMapper.findById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException("无权删除此房源");
        }

        // 删除房源（物理删除）
        houseMapper.deleteById(id);
        // 删除关联图片
        houseImageMapper.deleteByHouseId(id);
    }

    @Override
    public void updateStatus(Long id, Long landlordId, Integer status) {
        House house = houseMapper.findById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException("无权操作此房源");
        }

        houseMapper.updateStatus(id, status);
    }

    /**
     * 将House实体转换为VO（包含图片和房东信息）
     */
    private HouseVO convertToVO(House house) {
        HouseVO vo = new HouseVO();
        BeanUtils.copyProperties(house, vo);

        // 1. 查询房源图片
        List<HouseImage> images = houseImageMapper.findByHouseId(house.getId());
        List<String> imageUrls = images.stream()
                .map(HouseImage::getImageUrl)
                .collect(Collectors.toList());
        vo.setImageUrls(imageUrls);

        // 2. 设置封面图
        images.stream()
                .filter(img -> img.getType() == 1)
                .findFirst()
                .ifPresent(img -> vo.setCoverImage(img.getImageUrl()));

        // 3. 查询房东信息
        User landlord = userMapper.findById(house.getLandlordId());
        if (landlord != null) {
            vo.setLandlordName(landlord.getRealName());
            vo.setLandlordPhone(landlord.getPhone());
        }

        return vo;
    }
}