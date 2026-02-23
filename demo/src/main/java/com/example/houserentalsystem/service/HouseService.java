package com.example.houserentalsystem.service;

import com.example.houserentalsystem.dto.HousePublishDTO;
import com.example.houserentalsystem.dto.HouseQueryDTO;
import com.example.houserentalsystem.vo.HouseVO;
import java.util.List;
import java.util.Map;

public interface HouseService {
    
    // 发布房源
    HouseVO publish(Long landlordId, HousePublishDTO publishDTO);
    
    // 根据ID查询房源详情
    HouseVO getHouseDetail(Long id);
    
    // 分页查询房源
    Map<String, Object> pageQuery(HouseQueryDTO queryDTO);
    
    // 查询房东的房源列表
    List<HouseVO> getLandlordHouses(Long landlordId);
    
    // 更新房源
    HouseVO updateHouse(Long id, Long landlordId, HousePublishDTO updateDTO);
    
    // 删除房源
    void deleteHouse(Long id, Long landlordId);
    
    // 上架/下架
    void updateStatus(Long id, Long landlordId, Integer status);
}