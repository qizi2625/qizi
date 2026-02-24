package com.example.houserentalsystem.controller;

import com.example.houserentalsystem.annotation.RequireToken;
import com.example.houserentalsystem.common.Result;
import com.example.houserentalsystem.dto.TagDTO;
import com.example.houserentalsystem.service.TagService;
import com.example.houserentalsystem.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 创建标签（管理员）
     */
    @PostMapping("/create")
    @RequireToken
    public Result<TagVO> createTag(@RequestBody TagDTO tagDTO) {
        TagVO tagVO = tagService.createTag(tagDTO);
        return Result.success("创建成功", tagVO);
    }

    /**
     * 更新标签（管理员）
     */
    @PutMapping("/update/{id}")
    @RequireToken
    public Result<TagVO> updateTag(@PathVariable Long id, @RequestBody TagDTO tagDTO) {
        TagVO tagVO = tagService.updateTag(id, tagDTO);
        return Result.success("更新成功", tagVO);
    }

    /**
     * 删除标签（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @RequireToken
    public Result<String> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success("删除成功");
    }

    /**
     * 查询所有标签
     */
    @GetMapping("/list")
    public Result<List<TagVO>> getAllTags() {
        List<TagVO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 按分类查询标签
     */
    @GetMapping("/category/{category}")
    public Result<List<TagVO>> getTagsByCategory(@PathVariable String category) {
        List<TagVO> tags = tagService.getTagsByCategory(category);
        return Result.success(tags);
    }

    /**
     * 查询单个标签
     */
    @GetMapping("/{id}")
    public Result<TagVO> getTag(@PathVariable Long id) {
        TagVO tag = tagService.getTag(id);
        return Result.success(tag);
    }

    /**
     * 获取房源的标签
     */
    @GetMapping("/house/{houseId}")
    public Result<List<TagVO>> getHouseTags(@PathVariable Long houseId) {
        List<TagVO> tags = tagService.getHouseTags(houseId);
        return Result.success(tags);
    }
}