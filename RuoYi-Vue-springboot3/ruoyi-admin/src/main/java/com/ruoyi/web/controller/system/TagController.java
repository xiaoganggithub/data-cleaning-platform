package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.application.service.TagApplicationService;
import com.ruoyi.system.domain.entity.Tag;
import com.ruoyi.system.domain.entity.TagType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 标签管理接口
 */
@RestController
@RequestMapping("/system/tag")
public class TagController {

    private final TagApplicationService tagService;

    public TagController(TagApplicationService tagService) {
        this.tagService = tagService;
    }

    /**
     * 创建根标签
     */
    @PostMapping("/root")
    public AjaxResult createRootTag(@RequestBody CreateTagRequest request) {
        Tag tag = tagService.createRootTag(request.getName(), request.getType(), request.getDescription());
        return AjaxResult.success(tag);
    }

    /**
     * 创建子标签
     */
    @PostMapping("/child")
    public AjaxResult createChildTag(@RequestBody CreateChildTagRequest request) {
        Tag tag = tagService.createChildTag(
                request.getParentId(), request.getName(), request.getType(), request.getDescription());
        return AjaxResult.success(tag);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{tagId}")
    public AjaxResult updateTag(@PathVariable Long tagId, @RequestBody UpdateTagRequest request) {
        Tag tag = tagService.updateTag(tagId, request.getName(), request.getDescription());
        return AjaxResult.success(tag);
    }

    /**
     * 归档标签
     */
    @PostMapping("/{tagId}/archive")
    public AjaxResult archiveTag(@PathVariable Long tagId) {
        Tag tag = tagService.archiveTag(tagId);
        return AjaxResult.success(tag);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    public AjaxResult deleteTag(@PathVariable Long tagId) {
        boolean result = tagService.deleteTag(tagId);
        return result ? AjaxResult.success() : AjaxResult.error("删除失败");
    }

    /**
     * 查询标签
     */
    @GetMapping("/{tagId}")
    public AjaxResult getTag(@PathVariable Long tagId) {
        return tagService.findById(tagId)
                .map(AjaxResult::success)
                .orElse(AjaxResult.error("标签不存在"));
    }

    /**
     * 查询所有根标签
     */
    @GetMapping("/root/list")
    public AjaxResult listRootTags() {
        List<Tag> tags = tagService.findRootTags();
        return AjaxResult.success(tags);
    }

    /**
     * 查询子标签
     */
    @GetMapping("/children/{parentId}")
    public AjaxResult listChildren(@PathVariable Long parentId) {
        List<Tag> tags = tagService.findByParentId(parentId);
        return AjaxResult.success(tags);
    }

    /**
     * 根据类型查询标签
     */
    @GetMapping("/type/{type}")
    public AjaxResult listByType(@PathVariable Integer type) {
        TagType tagType = TagType.fromValue(type);
        List<Tag> tags = tagService.findByType(tagType);
        return AjaxResult.success(tags);
    }

    /**
     * 查询所有标签
     */
    @GetMapping("/list")
    public AjaxResult listTags() {
        List<Tag> tags = tagService.findAll();
        return AjaxResult.success(tags);
    }

    /**
     * 更新排序权重
     */
    @PutMapping("/{tagId}/sort-order")
    public AjaxResult updateSortOrder(@PathVariable Long tagId, @RequestParam Integer sortOrder) {
        tagService.updateSortOrder(tagId, sortOrder);
        return AjaxResult.success();
    }

    /**
     * 更新优先级
     */
    @PutMapping("/{tagId}/priority")
    public AjaxResult updatePriority(@PathVariable Long tagId, @RequestParam Integer priority) {
        tagService.updatePriority(tagId, priority);
        return AjaxResult.success();
    }

    /**
     * 统计标签数量
     */
    @GetMapping("/count")
    public AjaxResult countTags() {
        long count = tagService.count();
        return AjaxResult.success(count);
    }

    /**
     * 按类型统计
     */
    @GetMapping("/statistics/type")
    public AjaxResult statisticsByType() {
        Map<TagType, Long> statistics = tagService.countByType();
        return AjaxResult.success(statistics);
    }

    // ========== Request DTOs ==========

    public static class CreateTagRequest {
        private String name;
        private TagType type;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TagType getType() { return type; }
        public void setType(TagType type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class CreateChildTagRequest {
        private Long parentId;
        private String name;
        private TagType type;
        private String description;

        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TagType getType() { return type; }
        public void setType(TagType type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class UpdateTagRequest {
        private String name;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
