package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.application.service.ImageApplicationService;
import com.ruoyi.system.domain.entity.ProductImage;
import com.ruoyi.system.domain.entity.ImageStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品图片管理接口
 */
@RestController
@RequestMapping("/system/image")
public class ImageController {

    private final ImageApplicationService imageService;

    public ImageController(ImageApplicationService imageService) {
        this.imageService = imageService;
    }

    /**
     * 创建图片记录
     */
    @PostMapping("/create")
    public AjaxResult createImage(@RequestBody CreateImageRequest request) {
        ProductImage image = imageService.createImage(
                request.getDatasetId(),
                request.getDatasetCode(),
                request.getShopcode(),
                request.getVendorcode(),
                request.getSn(),
                request.getImageMd5(),
                request.getImageUrl()
        );
        return AjaxResult.success(image);
    }

    /**
     * 清洗图片
     */
    @PostMapping("/{imageId}/clean")
    public AjaxResult cleanImage(@PathVariable Long imageId, @RequestBody CleanImageRequest request) {
        ProductImage image = imageService.cleanImage(imageId, request.getProblemTypes(), request.getCleanRemark());
        return AjaxResult.success(image);
    }

    /**
     * 提交审核
     */
    @PostMapping("/{imageId}/submit-review")
    public AjaxResult submitForReview(@PathVariable Long imageId) {
        ProductImage image = imageService.submitForReview(imageId);
        return AjaxResult.success(image);
    }

    /**
     * 审核通过
     */
    @PostMapping("/{imageId}/approve")
    public AjaxResult approveImage(@PathVariable Long imageId) {
        ProductImage image = imageService.approveImage(imageId);
        return AjaxResult.success(image);
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/{imageId}/reject")
    public AjaxResult rejectImage(@PathVariable Long imageId, @RequestBody RejectImageRequest request) {
        ProductImage image = imageService.rejectImage(imageId, request.getRejectReason());
        return AjaxResult.success(image);
    }

    /**
     * 锁定图片
     */
    @PostMapping("/{imageId}/lock")
    public AjaxResult lockImage(@PathVariable Long imageId) {
        imageService.lockImage(imageId);
        return AjaxResult.success();
    }

    /**
     * 解锁图片
     */
    @PostMapping("/{imageId}/unlock")
    public AjaxResult unlockImage(@PathVariable Long imageId) {
        imageService.unlockImage(imageId);
        return AjaxResult.success();
    }

    /**
     * 设置质量评分
     */
    @PutMapping("/{imageId}/quality-score")
    public AjaxResult setQualityScore(@PathVariable Long imageId, @RequestParam Double qualityScore) {
        imageService.setQualityScore(imageId, qualityScore);
        return AjaxResult.success();
    }

    /**
     * 查询图片
     */
    @GetMapping("/{imageId}")
    public AjaxResult getImage(@PathVariable Long imageId) {
        return imageService.findById(imageId)
                .map(AjaxResult::success)
                .orElse(AjaxResult.error("图片不存在"));
    }

    /**
     * 根据数据集查询所有图片
     */
    @GetMapping("/dataset/{datasetId}")
    public AjaxResult listByDataset(@PathVariable Long datasetId) {
        List<ProductImage> images = imageService.findByDatasetId(datasetId);
        return AjaxResult.success(images);
    }

    /**
     * 查询待清洗图片
     */
    @GetMapping("/pending-cleaning")
    public AjaxResult listPendingCleaning() {
        List<ProductImage> images = imageService.findPendingCleaningImages();
        return AjaxResult.success(images);
    }

    /**
     * 查询已清洗图片
     */
    @GetMapping("/cleaned")
    public AjaxResult listCleaned() {
        List<ProductImage> images = imageService.findCleanedImages();
        return AjaxResult.success(images);
    }

    /**
     * 查询待审核图片
     */
    @GetMapping("/pending-review")
    public AjaxResult listPendingReview() {
        List<ProductImage> images = imageService.findPendingReviewImages();
        return AjaxResult.success(images);
    }

    /**
     * 查询已通过图片
     */
    @GetMapping("/approved")
    public AjaxResult listApproved() {
        List<ProductImage> images = imageService.findApprovedImages();
        return AjaxResult.success(images);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public AjaxResult pageImages(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        List<ProductImage> images = imageService.findByPage(page, size);
        return AjaxResult.success(images);
    }

    /**
     * 带过滤的分页查询
     */
    @GetMapping("/filter")
    public AjaxResult filterImages(@RequestParam(required = false) Long datasetId,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        ImageStatus imageStatus = status != null ? ImageStatus.fromValue(status) : null;
        List<ProductImage> images = imageService.findByFilter(datasetId, imageStatus, categoryId, page, size);
        return AjaxResult.success(images);
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/{imageId}")
    public AjaxResult deleteImage(@PathVariable Long imageId) {
        boolean result = imageService.deleteImage(imageId);
        return result ? AjaxResult.success() : AjaxResult.error("删除失败");
    }

    // ========== Request DTOs ==========

    public static class CreateImageRequest {
        private Long datasetId;
        private String datasetCode;
        private String shopcode;
        private String vendorcode;
        private String sn;
        private String imageMd5;
        private String imageUrl;

        public Long getDatasetId() { return datasetId; }
        public void setDatasetId(Long datasetId) { this.datasetId = datasetId; }
        public String getDatasetCode() { return datasetCode; }
        public void setDatasetCode(String datasetCode) { this.datasetCode = datasetCode; }
        public String getShopcode() { return shopcode; }
        public void setShopcode(String shopcode) { this.shopcode = shopcode; }
        public String getVendorcode() { return vendorcode; }
        public void setVendorcode(String vendorcode) { this.vendorcode = vendorcode; }
        public String getSn() { return sn; }
        public void setSn(String sn) { this.sn = sn; }
        public String getImageMd5() { return imageMd5; }
        public void setImageMd5(String imageMd5) { this.imageMd5 = imageMd5; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class CleanImageRequest {
        private String problemTypes;
        private String cleanRemark;

        public String getProblemTypes() { return problemTypes; }
        public void setProblemTypes(String problemTypes) { this.problemTypes = problemTypes; }
        public String getCleanRemark() { return cleanRemark; }
        public void setCleanRemark(String cleanRemark) { this.cleanRemark = cleanRemark; }
    }

    public static class RejectImageRequest {
        private String rejectReason;

        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    }
}
