package com.ruoyi.system.domain.repository;

import com.ruoyi.system.domain.entity.ProductCategory;
import com.ruoyi.system.domain.entity.CategoryStatus;
import java.util.Optional;
import java.util.List;

/**
 * 商品分类领域仓储接口
 * 定义商品分类相关的数据访问操作
 */
public interface ProductCategoryRepository {

    /**
     * 保存商品分类
     * @param category 商品分类实体
     * @return 保存后的商品分类
     */
    ProductCategory save(ProductCategory category);

    /**
     * 根据ID查询商品分类
     * @param id 商品分类ID
     * @return 商品分类实体
     */
    Optional<ProductCategory> findById(Long id);

    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 商品分类列表
     */
    List<ProductCategory> findByParentId(Long parentId);

    /**
     * 根据状态查询商品分类
     * @param status 状态
     * @return 商品分类列表
     */
    List<ProductCategory> findByStatus(CategoryStatus status);

    /**
     * 查询所有根分类
     * @return 根分类列表
     */
    List<ProductCategory> findRootCategories();

    /**
     * 查询所有叶子分类
     * @return 叶子分类列表
     */
    List<ProductCategory> findLeafCategories();

    /**
     * 按排序权重查询商品分类列表
     * @return 商品分类列表
     */
    List<ProductCategory> findByOrderBySortOrderAsc();

    /**
     * 根据分类名称查询
     * @param name 分类名称
     * @return 商品分类实体
     */
    Optional<ProductCategory> findByName(String name);

    /**
     * 查询所有商品分类
     * @return 商品分类列表
     */
    List<ProductCategory> findAll();

    /**
     * 分页查询商品分类
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 商品分类分页结果
     */
    List<ProductCategory> findByPage(int page, int size);

    /**
     * 统计商品分类数量
     * @return 商品分类总数
     */
    long count();

    /**
     * 统计各状态商品分类数量
     * @return 状态统计
     */
    java.util.Map<CategoryStatus, Long> countByStatus();

    /**
     * 删除商品分类
     * @param id 商品分类ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除商品分类
     * @param ids 商品分类ID列表
     * @return 删除数量
     */
    int deleteByIds(List<Long> ids);

    /**
     * 检查名称是否存在
     * @param name 分类名称
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 检查编码是否存在
     * @param code 分类编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
