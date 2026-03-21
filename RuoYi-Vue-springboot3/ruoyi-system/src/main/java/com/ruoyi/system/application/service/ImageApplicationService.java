package com.ruoyi.system.application.service;

import com.ruoyi.system.domain.entity.ProductImage;
import com.ruoyi.system.domain.entity.ImageStatus;
import com.ruoyi.system.domain.repository.ProductImageRepository;
import com.ruoyi.system.domain.service.ImageDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * 商品图片应用服务
 */
@Service
public class ImageApplicationService {

    private final ProductImageRepository imageRepository;

    public ImageApplicationService(ProductImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * 创建图片记录
     */
    @Transactional
    public ProductImage createImage(Long datasetId, String datasetCode, String shopcode,
                                   String vendorcode, String sn, String imageMd5, String imageUrl) {
        // Validate
        if (!ImageDomainService.isValidImageMd5(imageMd5)) {
            throw new IllegalArgumentException("无效的图片MD5格式");
        }
        if (!ImageDomainService.isValidImageUrl(imageUrl)) {
            throw new IllegalArgumentException("无效的图片URL格式");
        }

        // Check for duplicates
        if (imageRepository.existsByDatasetIdAndImageMd5(datasetId, imageMd5)) {
            throw new IllegalArgumentException("图片已存在");
        }

        ProductImage image = new ProductImage(datasetId, datasetCode, shopcode,
                vendorcode, sn, imageMd5, imageUrl);

        return imageRepository.save(image);
    }

    /**
     * 清洗图片
     */
    @Transactional
    public ProductImage cleanImage(Long imageId, String problemTypes, String cleanRemark) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        ImageDomainService.validateCleanRemark(cleanRemark);
        ImageDomainService.validateProblemTypes(problemTypes);

        image.cleanImage(problemTypes, cleanRemark);

        return imageRepository.save(image);
    }

    /**
     * 提交审核
     */
    @Transactional
    public ProductImage submitForReview(Long imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        image.submitForReview();

        return imageRepository.save(image);
    }

    /**
     * 审核通过
     */
    @Transactional
    public ProductImage approveImage(Long imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        image.approve();

        return imageRepository.save(image);
    }

    /**
     * 审核拒绝
     */
    @Transactional
    public ProductImage rejectImage(Long imageId, String rejectReason) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        image.reject(rejectReason);

        return imageRepository.save(image);
    }

    /**
     * 锁定图片
     */
    @Transactional
    public void lockImage(Long imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        image.lock();
        imageRepository.save(image);
    }

    /**
     * 解锁图片
     */
    @Transactional
    public void unlockImage(Long imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        image.unlock();
        imageRepository.save(image);
    }

    /**
     * 设置质量评分
     */
    @Transactional
    public void setQualityScore(Long imageId, Double qualityScore) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在"));

        ImageDomainService.validateQualityScore(qualityScore);

        image.setQualityScore(qualityScore);
        imageRepository.save(image);
    }

    /**
     * 查询图片
     */
    public Optional<ProductImage> findById(Long imageId) {
        return imageRepository.findById(imageId);
    }

    /**
     * 根据数据集查询所有图片
     */
    public List<ProductImage> findByDatasetId(Long datasetId) {
        return imageRepository.findByDatasetId(datasetId);
    }

    /**
     * 根据状态查询待清洗图片
     */
    public List<ProductImage> findPendingCleaningImages() {
        return imageRepository.findPendingCleaningImages();
    }

    /**
     * 根据状态查询已清洗图片
     */
    public List<ProductImage> findCleanedImages() {
        return imageRepository.findCleanedImages();
    }

    /**
     * 根据状态查询待审核图片
     */
    public List<ProductImage> findPendingReviewImages() {
        return imageRepository.findPendingReviewImages();
    }

    /**
     * 根据状态查询已通过图片
     */
    public List<ProductImage> findApprovedImages() {
        return imageRepository.findApprovedImages();
    }

    /**
     * 分页查询
     */
    public List<ProductImage> findByPage(int page, int size) {
        return imageRepository.findByPage(page, size);
    }

    /**
     * 带过滤条件的分页查询
     */
    public List<ProductImage> findByFilter(Long datasetId, ImageStatus status,
                                          Long categoryId, int page, int size) {
        return imageRepository.findByFilter(datasetId, status, categoryId, page, size);
    }

    /**
     * 统计数量
     */
    public long count() {
        return imageRepository.count();
    }

    /**
     * 按数据集统计数量
     */
    public long countByDatasetId(Long datasetId) {
        return imageRepository.countByDatasetId(datasetId);
    }

    /**
     * 按状态统计
     */
    public Map<ImageStatus, Long> countByStatus() {
        return imageRepository.countByStatus();
    }

    /**
     * 删除图片
     */
    @Transactional
    public boolean deleteImage(Long imageId) {
        return imageRepository.deleteById(imageId);
    }
}
