package com.ruoyi.system.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.entity.ProductCategory;
import com.ruoyi.system.domain.repository.ProductCategoryRepository;
import com.ruoyi.system.persistence.mapper.ProductCategoryMapper;
import com.ruoyi.system.persistence.po.ProductCategoryPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 商品分类仓储实现
 */
@Repository
public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {

    private final ProductCategoryMapper categoryMapper;

    public ProductCategoryRepositoryImpl(ProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ProductCategory save(ProductCategory category) {
        ProductCategoryPO po = toPO(category);
        if (category.getCategoryId() == null) {
            categoryMapper.insert(po);
        } else {
            categoryMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<ProductCategory> findById(Long id) {
        ProductCategoryPO po = categoryMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<ProductCategory> findByParentId(Long parentId) {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(parentId == null, ProductCategoryPO::getParentId, null);
        wrapper.ne(parentId != null, ProductCategoryPO::getParentId, parentId);
        if (parentId != null) {
            wrapper.eq(ProductCategoryPO::getParentId, parentId);
        }
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findByStatus(ProductCategory.CategoryStatus status) {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategoryPO::getStatus, status.getValue());
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findRootCategories() {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ProductCategoryPO::getParentId);
        wrapper.eq(ProductCategoryPO::getStatus, ProductCategory.CategoryStatus.NORMAL.getValue());
        wrapper.orderByAsc(ProductCategoryPO::getSortOrder);
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findLeafCategories() {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategoryPO::getChildCount, 0);
        wrapper.eq(ProductCategoryPO::getStatus, ProductCategory.CategoryStatus.NORMAL.getValue());
        wrapper.orderByAsc(ProductCategoryPO::getSortOrder);
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findByOrderBySortOrderAsc() {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ProductCategoryPO::getSortOrder);
        return categoryMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductCategory> findByName(String name) {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategoryPO::getName, name);
        ProductCategoryPO po = categoryMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<ProductCategory> findAll() {
        return categoryMapper.selectList(null).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findByPage(int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductCategoryPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        categoryMapper.selectPage(pageObj, null);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return categoryMapper.selectCount(null);
    }

    @Override
    public Map<ProductCategory.CategoryStatus, Long> countByStatus() {
        List<ProductCategoryPO> all = categoryMapper.selectList(null);
        Map<ProductCategory.CategoryStatus, Long> result = new HashMap<>();
        for (ProductCategory.CategoryStatus status : ProductCategory.CategoryStatus.values()) {
            result.put(status, 0L);
        }
        for (ProductCategoryPO po : all) {
            ProductCategory.CategoryStatus status = ProductCategory.CategoryStatus.fromValue(po.getStatus());
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return categoryMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategoryPO::getName, name);
        if (excludeId != null) {
            wrapper.ne(ProductCategoryPO::getCategoryId, excludeId);
        }
        return categoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<ProductCategoryPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategoryPO::getCategoryCode, code);
        return categoryMapper.selectCount(wrapper) > 0;
    }

    // ========== Assembler Methods ==========

    private ProductCategory toEntity(ProductCategoryPO po) {
        return ProductCategory.rebuildFromPO(po);
    }

    private ProductCategoryPO toPO(ProductCategory entity) {
        ProductCategoryPO po = new ProductCategoryPO();
        po.setCategoryId(entity.getCategoryId());
        po.setCategoryCode(entity.getCategoryCode() != null ? entity.getCategoryCode().getValue() : null);
        po.setParentId(entity.getParentId());
        po.setName(entity.getName());
        po.setDescription(entity.getDescription());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getValue() : null);
        po.setImageCount(entity.getImageCount() != null ? entity.getImageCount().getValue() : null);
        po.setChildCount(entity.getChildCount());
        po.setSortOrder(entity.getSortOrder());
        po.setIcon(entity.getIcon());
        po.setColor(entity.getColor());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }
}
