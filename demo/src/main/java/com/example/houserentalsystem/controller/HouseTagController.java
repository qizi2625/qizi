package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.dto.HouseTagsDTO;
import com.example.houserentalsystem.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/house/tag")
public class HouseTagController {

    @Autowired
    private TagService tagService;

    /**
     * 给房源添加标签（房东/管理员）
     */
    @PostMapping("/add")
    @RequireToken
    public Result<String> addTagsToHouse(@RequestBody HouseTagsDTO houseTagsDTO) {
        tagService.addTagsToHouse(houseTagsDTO.getHouseId(), houseTagsDTO.getTagIds());
        return Result.success("添加成功");
    }

    /**
     * 移除房源标签
     */
    @DeleteMapping("/remove/{houseId}/{tagId}")
    @RequireToken
    public Result<String> removeTagFromHouse(@PathVariable Long houseId, @PathVariable Long tagId) {
        tagService.removeTagFromHouse(houseId, tagId);
        return Result.success("移除成功");
    }
}