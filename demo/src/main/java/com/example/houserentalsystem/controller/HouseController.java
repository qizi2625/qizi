package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.BusinessException;
import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.dto.HousePublishDTO;
import com.example.houserentalsystem.dto.HouseQueryDTO;
import com.example.houserentalsystem.entity.User;
import com.example.houserentalsystem.service.HouseService;
import com.example.houserentalsystem.service.UserService;
import com.example.houserentalsystem.vo.HouseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    /**
     * 发布房源（需要房东权限）
     */
    @PostMapping("/publish")
    @RequireToken
    public Result<HouseVO> publish(HttpServletRequest request, @RequestBody HousePublishDTO publishDTO) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 检查是否为房东
        User user = userService.getById(userId);
        if (user.getRoleId() != 2) {
            throw new BusinessException("只有房东才能发布房源");
        }

        HouseVO houseVO = houseService.publish(userId, publishDTO);
        return Result.success("发布成功", houseVO);
    }

    /**
     * 获取房源详情
     */
    @GetMapping("/detail/{id}")
    public Result<HouseVO> getDetail(@PathVariable Long id) {
        HouseVO houseVO = houseService.getHouseDetail(id);
        return Result.success(houseVO);
    }

    /**
     * 分页查询房源
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(HouseQueryDTO queryDTO) {
        Map<String, Object> pageResult = houseService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取当前房东的房源列表
     */
    @GetMapping("/my-houses")
    @RequireToken
    public Result<List<HouseVO>> getMyHouses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 检查是否为房东
        User user = userService.getById(userId);
        if (user.getRoleId() != 2) {
            throw new BusinessException("只有房东才能查看自己的房源");
        }

        List<HouseVO> houses = houseService.getLandlordHouses(userId);
        return Result.success(houses);
    }

    /**
     * 更新房源
     */
    @PutMapping("/update/{id}")
    @RequireToken
    public Result<HouseVO> update(HttpServletRequest request, 
                                  @PathVariable Long id, 
                                  @RequestBody HousePublishDTO updateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 检查是否为房东
        User user = userService.getById(userId);
        if (user.getRoleId() != 2) {
            throw new BusinessException("只有房东才能修改房源");
        }

        HouseVO houseVO = houseService.updateHouse(id, userId, updateDTO);
        return Result.success("更新成功", houseVO);
    }

    /**
     * 删除房源
     */
    @DeleteMapping("/delete/{id}")
    @RequireToken
    public Result<String> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 检查是否为房东
        User user = userService.getById(userId);
        if (user.getRoleId() != 2) {
            throw new BusinessException("只有房东才能删除房源");
        }

        houseService.deleteHouse(id, userId);
        return Result.success("删除成功");
    }

    /**
     * 上架房源
     */
    @PutMapping("/online/{id}")
    @RequireToken
    public Result<String> online(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 检查是否为房东
        User user = userService.getById(userId);
        if (user.getRoleId() != 2) {
            throw new BusinessException("只有房东才能操作房源");
        }

        houseService.updateStatus(id, userId, 1);
        return Result.success("上架成功");
    }

    /**
     * 下架房源
     */
    @PutMapping("/offline/{id}")
    @RequireToken
    public Result<String> offline(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 检查是否为房东
        User user = userService.getById(userId);
        if (user.getRoleId() != 2) {
            throw new BusinessException("只有房东才能操作房源");
        }

        houseService.updateStatus(id, userId, 2);
        return Result.success("下架成功");
    }
}