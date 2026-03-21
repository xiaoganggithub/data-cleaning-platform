package com.ruoyi.system.application.service;

import com.ruoyi.system.domain.entity.ProductCategory;
import com.ruoyi.system.domain.repository.ProductCategoryRepository;
import com.ruoyi.system.domain.service.CategoryDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * 商品分类应用服务
 */
@Service
public class CategoryApplicationService {

    private final ProductCategoryRepository categoryRepository;

    public CategoryApplicationService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 创建根分类
     */
    @Transactional
    public ProductCategory createRootCategory(String name, String description) {
        CategoryDomainService.validateCategoryName(name);
        CategoryDomainService.validateCategoryDescription(description);

        ProductCategory category = new ProductCategory(name, description);
        return categoryRepository.save(category);
    }

    /**
     * 创建子分类
     */
    @Transactional
    public ProductCategory createChildCategory(Long parentId, String name, String description) {
        CategoryDomainService.validateCategoryName(name);
        CategoryDomainService.validateCategoryDescription(description);
        CategoryDomainService.validateParentId(parentId);

        ProductCategory category = new ProductCategory(parentId, name, description);
        return categoryRepository.save(category);
    }

    /**
     * 更新分类
     */
    @Transactional
    public ProductCategory updateCategory(Long categoryId, String name, String description) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));

        CategoryDomainService.validateCategoryName(name);
        CategoryDomainService.validateCategoryDescription(description);

        category.setName(name);
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    /**
     * 归档分类
     */
    @Transactional
    public ProductCategory archiveCategory(Long categoryId) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));

        category.archive();

        return categoryRepository.save(category);
    }

    /**
     * 增加图像数量
     */
    @Transactional
    public void addImageCount(Long categoryId, int count) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));

        category.addImageCount(count);
        categoryRepository.save(category);
    }

    /**
     * 减少图像数量
     */
    @Transactional
    public void removeImageCount(Long categoryId, int count) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));

        category.removeImageCount(count);
        categoryRepository.save(category);
    }

    /**
     * 更新排序权重
     */
    @Transactional
    public void updateSortOrder(Long categoryId, Integer sortOrder) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));

        category.updateSortOrder(sortOrder);
        categoryRepository.save(category);
    }

    /**
     * 查询分类
     */
    public Optional<ProductCategory> findById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    /**
     * 查询所有根分类
     */
    public List<ProductCategory> findRootCategories() {
        return categoryRepository.findRootCategories();
    }

    /**
     * 查询子分类
     */
    public List<ProductCategory> findByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    /**
     * 查询所有分类
     */
    public List<ProductCategory> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * 根据状态查询
     */
    public List<ProductCategory> findByStatus(ProductCategory.CategoryStatus status) {
        return categoryRepository.findByStatus(status);
    }

    /**
     * 删除分类
     */
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        return categoryRepository.deleteById(categoryId);
    }

    /**
     * 统计数量
     */
    public long count() {
        return categoryRepository.count();
    }
}
