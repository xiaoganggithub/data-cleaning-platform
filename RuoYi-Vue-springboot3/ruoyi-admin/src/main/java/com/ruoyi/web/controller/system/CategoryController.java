package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.application.service.CategoryApplicationService;
import com.ruoyi.system.domain.entity.ProductCategory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理接口
 */
@RestController
@RequestMapping("/system/category")
public class CategoryController {

    private final CategoryApplicationService categoryService;

    public CategoryController(CategoryApplicationService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 创建根分类
     */
    @PostMapping("/root")
    public AjaxResult createRootCategory(@RequestBody CreateCategoryRequest request) {
        ProductCategory category = categoryService.createRootCategory(request.getName(), request.getDescription());
        return AjaxResult.success(category);
    }

    /**
     * 创建子分类
     */
    @PostMapping("/child")
    public AjaxResult createChildCategory(@RequestBody CreateChildCategoryRequest request) {
        ProductCategory category = categoryService.createChildCategory(
                request.getParentId(), request.getName(), request.getDescription());
        return AjaxResult.success(category);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    public AjaxResult updateCategory(@PathVariable Long categoryId, @RequestBody UpdateCategoryRequest request) {
        ProductCategory category = categoryService.updateCategory(
                categoryId, request.getName(), request.getDescription());
        return AjaxResult.success(category);
    }

    /**
     * 归档分类
     */
    @PostMapping("/{categoryId}/archive")
    public AjaxResult archiveCategory(@PathVariable Long categoryId) {
        ProductCategory category = categoryService.archiveCategory(categoryId);
        return AjaxResult.success(category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public AjaxResult deleteCategory(@PathVariable Long categoryId) {
        boolean result = categoryService.deleteCategory(categoryId);
        return result ? AjaxResult.success() : AjaxResult.error("删除失败");
    }

    /**
     * 查询分类
     */
    @GetMapping("/{categoryId}")
    public AjaxResult getCategory(@PathVariable Long categoryId) {
        return categoryService.findById(categoryId)
                .map(AjaxResult::success)
                .orElse(AjaxResult.error("分类不存在"));
    }

    /**
     * 查询所有根分类
     */
    @GetMapping("/root/list")
    public AjaxResult listRootCategories() {
        List<ProductCategory> categories = categoryService.findRootCategories();
        return AjaxResult.success(categories);
    }

    /**
     * 查询子分类
     */
    @GetMapping("/children/{parentId}")
    public AjaxResult listChildren(@PathVariable Long parentId) {
        List<ProductCategory> categories = categoryService.findByParentId(parentId);
        return AjaxResult.success(categories);
    }

    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public AjaxResult listCategories() {
        List<ProductCategory> categories = categoryService.findAll();
        return AjaxResult.success(categories);
    }

    /**
     * 更新排序权重
     */
    @PutMapping("/{categoryId}/sort-order")
    public AjaxResult updateSortOrder(@PathVariable Long categoryId, @RequestParam Integer sortOrder) {
        categoryService.updateSortOrder(categoryId, sortOrder);
        return AjaxResult.success();
    }

    // ========== Request DTOs ==========

    public static class CreateCategoryRequest {
        private String name;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class CreateChildCategoryRequest {
        private Long parentId;
        private String name;
        private String description;

        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class UpdateCategoryRequest {
        private String name;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
