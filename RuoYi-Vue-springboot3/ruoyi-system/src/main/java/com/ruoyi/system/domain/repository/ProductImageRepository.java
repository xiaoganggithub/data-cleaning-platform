package com.ruoyi.system.domain.repository;

import com.ruoyi.system.domain.entity.ProductImage;
import java.util.Optional;
import java.util.List;

/**
 * 商品图片领域仓储接口
 * 定义商品图片相关的数据访问操作
 */
public interface ProductImageRepository {

    /**
     * 保存商品图片
     * @param image 商品图片实体
     * @return 保存后的商品图片
     */
    ProductImage save(ProductImage image);

    /**
     * 批量保存商品图片
     * @param images 商品图片列表
     * @return 保存后的商品图片列表
     */
    List<ProductImage> saveAll(List<ProductImage> images);

    /**
     * 根据ID查询商品图片
     * @param id 商品图片ID
     * @return 商品图片实体
     */
    Optional<ProductImage> findById(Long id);

    /**
     * 根据数据集ID查询商品图片
     * @param datasetId 数据集ID
     * @return 商品图片列表
     */
    List<ProductImage> findByDatasetId(Long datasetId);

    /**
     * 根据数据集编码查询商品图片
     * @param datasetCode 数据集编码
     * @return 商品图片列表
     */
    List<ProductImage> findByDatasetCode(String datasetCode);

    /**
     * 根据状态查询商品图片
     * @param status 状态
     * @return 商品图片列表
     */
    List<ProductImage> findByStatus(ProductImage.ImageStatus status);

    /**
     * 根据分类ID查询商品图片
     * @param categoryId 分类ID
     * @return 商品图片列表
     */
    List<ProductImage> findByCategoryId(Long categoryId);

    /**
     * 根据MD5查询商品图片
     * @param imageMd5 图片MD5
     * @return 商品图片实体
     */
    Optional<ProductImage> findByImageMd5(String imageMd5);

    /**
     * 根据数据集ID和MD5查询商品图片
     * @param datasetId 数据集ID
     * @param imageMd5 图片MD5
     * @return 商品图片实体
     */
    Optional<ProductImage> findByDatasetIdAndImageMd5(Long datasetId, String imageMd5);

    /**
     * 根据URL查询商品图片
     * @param imageUrl 图片URL
     * @return 商品图片实体
     */
    Optional<ProductImage> findByImageUrl(String imageUrl);

    /**
     * 查询待清洗的图片
     * @return 商品图片列表
     */
    List<ProductImage> findPendingCleaningImages();

    /**
     * 查询已清洗的图片
     * @return 商品图片列表
     */
    List<ProductImage> findCleanedImages();

    /**
     * 查询待审核的图片
     * @return 商品图片列表
     */
    List<ProductImage> findPendingReviewImages();

    /**
     * 查询已通过的图片
     * @return 商品图片列表
     */
    List<ProductImage> findApprovedImages();

    /**
     * 查询已拒绝的图片
     * @return 商品图片列表
     */
    List<ProductImage> findRejectedImages();

    /**
     * 查询已锁定的图片
     * @return 商品图片列表
     */
    List<ProductImage> findLockedImages();

    /**
     * 查询所有商品图片
     * @return 商品图片列表
     */
    List<ProductImage> findAll();

    /**
     * 根据数据集ID查询商品图片
     * @param datasetId 数据集ID
     * @return 商品图片列表
     */
    List<ProductImage> findAllByDatasetId(Long datasetId);

    /**
     * 根据数据集ID和状态查询商品图片
     * @param datasetId 数据集ID
     * @param status 状态
     * @return 商品图片列表
     */
    List<ProductImage> findByDatasetIdAndStatus(Long datasetId, ProductImage.ImageStatus status);

    /**
     * 根据MD5批量查询商品图片
     * @param imageMd5s 图片MD5列表
     * @return 商品图片列表
     */
    List<ProductImage> findByImageMd5s(List<String> imageMd5s);

    /**
     * 分页查询商品图片
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 商品图片分页结果
     */
    List<ProductImage> findByPage(int page, int size);

    /**
     * 分页查询商品图片（带过滤条件）
     * @param datasetId 数据集ID
     * @param status 状态
     * @param categoryId 分类ID
     * @param page 页码
     * @param size 每页大小
     * @return 商品图片列表
     */
    List<ProductImage> findByFilter(Long datasetId, ProductImage.ImageStatus status, Long categoryId, int page, int size);

    /**
     * 统计商品图片数量
     * @return 商品图片总数
     */
    long count();

    /**
     * 根据数据集ID统计商品图片数量
     * @param datasetId 数据集ID
     * @return 商品图片总数
     */
    long countByDatasetId(Long datasetId);

    /**
     * 根据数据集ID和状态统计商品图片数量
     * @param datasetId 数据集ID
     * @param status 状态
     * @return 商品图片总数
     */
    long countByDatasetIdAndStatus(Long datasetId, ProductImage.ImageStatus status);

    /**
     * 统计各状态商品图片数量
     * @return 状态统计
     */
    java.util.Map<ProductImage.ImageStatus, Long> countByStatus();

    /**
     * 统计各状态商品图片数量（按数据集）
     * @param datasetId 数据集ID
     * @return 状态统计
     */
    java.util.Map<ProductImage.ImageStatus, Long> countByStatus(Long datasetId);

    /**
     * 删除商品图片
     * @param id 商品图片ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除商品图片
     * @param ids 商品图片ID列表
     * @return 删除数量
     */
    int deleteByIds(List<Long> ids);

    /**
     * 根据数据集ID删除商品图片
     * @param datasetId 数据集ID
     * @return 删除数量
     */
    int deleteByDatasetId(Long datasetId);

    /**
     * 根据分类ID删除商品图片
     * @param categoryId 分类ID
     * @return 删除数量
     */
    int deleteByCategoryId(Long categoryId);

    /**
     * 检查MD5是否存在
     * @param imageMd5 图片MD5
     * @return 是否存在
     */
    boolean existsByImageMd5(String imageMd5);

    /**
     * 检查URL是否存在
     * @param imageUrl 图片URL
     * @return 是否存在
     */
    boolean existsByImageUrl(String imageUrl);

    /**
     * 检查数据集ID和MD5组合是否存在
     * @param datasetId 数据集ID
     * @param imageMd5 图片MD5
     * @return 是否存在
     */
    boolean existsByDatasetIdAndImageMd5(Long datasetId, String imageMd5);
}
