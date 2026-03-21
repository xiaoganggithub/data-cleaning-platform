package com.ruoyi.system.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.entity.ProductImage;
import com.ruoyi.system.domain.entity.ImageStatus;
import com.ruoyi.system.domain.repository.ProductImageRepository;
import com.ruoyi.system.persistence.mapper.ProductImageMapper;
import com.ruoyi.system.persistence.po.ProductImagePO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 商品图片仓储实现
 */
@Repository
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final ProductImageMapper imageMapper;

    public ProductImageRepositoryImpl(ProductImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public ProductImage save(ProductImage image) {
        ProductImagePO po = toPO(image);
        if (image.getImageId() == null) {
            imageMapper.insert(po);
        } else {
            imageMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public List<ProductImage> saveAll(List<ProductImage> images) {
        List<ProductImagePO> pos = images.stream().map(this::toPO).collect(Collectors.toList());
        // Batch insert using MyBatis-Plus
        for (ProductImagePO po : pos) {
            if (po.getImageId() == null) {
                imageMapper.insert(po);
            } else {
                imageMapper.updateById(po);
            }
        }
        return images;
    }

    @Override
    public Optional<ProductImage> findById(Long id) {
        ProductImagePO po = imageMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<ProductImage> findByDatasetId(Long datasetId) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findByDatasetCode(String datasetCode) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetCode, datasetCode);
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findByStatus(ImageStatus status) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getImageStatus, status.getValue());
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getCategoryId, categoryId);
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductImage> findByImageMd5(String imageMd5) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getImageMd5, imageMd5);
        ProductImagePO po = imageMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public Optional<ProductImage> findByDatasetIdAndImageMd5(Long datasetId, String imageMd5) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        wrapper.eq(ProductImagePO::getImageMd5, imageMd5);
        ProductImagePO po = imageMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public Optional<ProductImage> findByImageUrl(String imageUrl) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getImageUrl, imageUrl);
        ProductImagePO po = imageMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<ProductImage> findPendingCleaningImages() {
        return findByStatus(ImageStatus.PENDING_CLEANING);
    }

    @Override
    public List<ProductImage> findCleanedImages() {
        return findByStatus(ImageStatus.CLEANED);
    }

    @Override
    public List<ProductImage> findPendingReviewImages() {
        return findByStatus(ImageStatus.PENDING_REVIEW);
    }

    @Override
    public List<ProductImage> findApprovedImages() {
        return findByStatus(ImageStatus.APPROVED);
    }

    @Override
    public List<ProductImage> findRejectedImages() {
        return findByStatus(ImageStatus.REJECTED);
    }

    @Override
    public List<ProductImage> findLockedImages() {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getLocked, true);
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findAll() {
        return imageMapper.selectList(null).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findAllByDatasetId(Long datasetId) {
        return findByDatasetId(datasetId);
    }

    @Override
    public List<ProductImage> findByDatasetIdAndStatus(Long datasetId, ImageStatus status) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        wrapper.eq(ProductImagePO::getImageStatus, status.getValue());
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findByImageMd5s(List<String> imageMd5s) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductImagePO::getImageMd5, imageMd5s);
        return imageMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findByPage(int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductImagePO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        imageMapper.selectPage(pageObj, null);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findByFilter(Long datasetId, ImageStatus status, Long categoryId, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ProductImagePO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        if (datasetId != null) {
            wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        }
        if (status != null) {
            wrapper.eq(ProductImagePO::getImageStatus, status.getValue());
        }
        if (categoryId != null) {
            wrapper.eq(ProductImagePO::getCategoryId, categoryId);
        }
        imageMapper.selectPage(pageObj, wrapper);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return imageMapper.selectCount(null);
    }

    @Override
    public long countByDatasetId(Long datasetId) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        return imageMapper.selectCount(wrapper);
    }

    @Override
    public long countByDatasetIdAndStatus(Long datasetId, ImageStatus status) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        wrapper.eq(ProductImagePO::getImageStatus, status.getValue());
        return imageMapper.selectCount(wrapper);
    }

    @Override
    public Map<ImageStatus, Long> countByStatus() {
        List<ProductImagePO> all = imageMapper.selectList(null);
        Map<ImageStatus, Long> result = new HashMap<>();
        for (ImageStatus status : ImageStatus.values()) {
            result.put(status, 0L);
        }
        for (ProductImagePO po : all) {
            ImageStatus status = ImageStatus.fromValue(po.getImageStatus());
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public Map<ImageStatus, Long> countByStatus(Long datasetId) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        List<ProductImagePO> all = imageMapper.selectList(wrapper);
        Map<ImageStatus, Long> result = new HashMap<>();
        for (ImageStatus status : ImageStatus.values()) {
            result.put(status, 0L);
        }
        for (ProductImagePO po : all) {
            ImageStatus status = ImageStatus.fromValue(po.getImageStatus());
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        return imageMapper.deleteById(id) > 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return imageMapper.deleteBatchIds(ids);
    }

    @Override
    public int deleteByDatasetId(Long datasetId) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        return imageMapper.delete(wrapper);
    }

    @Override
    public int deleteByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getCategoryId, categoryId);
        return imageMapper.delete(wrapper);
    }

    @Override
    public boolean existsByImageMd5(String imageMd5) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getImageMd5, imageMd5);
        return imageMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByImageUrl(String imageUrl) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getImageUrl, imageUrl);
        return imageMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByDatasetIdAndImageMd5(Long datasetId, String imageMd5) {
        LambdaQueryWrapper<ProductImagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImagePO::getDatasetId, datasetId);
        wrapper.eq(ProductImagePO::getImageMd5, imageMd5);
        return imageMapper.selectCount(wrapper) > 0;
    }

    // ========== Assembler Methods ==========

    private ProductImage toEntity(ProductImagePO po) {
        return ProductImage.rebuildFromPO(po);
    }

    private ProductImagePO toPO(ProductImage entity) {
        ProductImagePO po = new ProductImagePO();
        po.setImageId(entity.getImageId());
        po.setDatasetId(entity.getDatasetId());
        po.setDatasetCode(entity.getDatasetCode());
        po.setCategoryId(entity.getCategoryId());
        po.setCategoryName(entity.getCategoryName());
        po.setPluCode(entity.getPluCode());
        po.setPluName(entity.getPluName());
        po.setShardId(entity.getShardId());
        po.setShopcode(entity.getShopcode());
        po.setVendorcode(entity.getVendorcode());
        po.setSn(entity.getSn());
        po.setImageMd5(entity.getImageMd5());
        po.setImageUrl(entity.getImageUrl());
        po.setImageStatus(entity.getImageStatus() != null ? entity.getImageStatus().getValue() : null);
        po.setLocked(entity.getLocked());
        po.setQualityScore(entity.getQualityScore());
        po.setCleanRemark(entity.getCleanRemark());
        po.setProblemTypes(entity.getProblemTypes());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }
}
