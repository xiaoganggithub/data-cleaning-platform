package com.ruoyi.system.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 商品图片领域实体 (充血模型)
 */
@Data
public class ProductImage implements Serializable {

    /**
     * 领域标识
     */
    private Long imageId;

    /**
     * 数据集ID
     */
    private Long datasetId;

    /**
     * 数据集编码
     */
    private String datasetCode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * PLU编码
     */
    private String pluCode;

    /**
     * PLU名称
     */
    private String pluName;

    /**
     * 数据分片ID
     */
    private Long shardId;

    /**
     * 店铺编码
     */
    private String shopcode;

    /**
     * 供应商编码
     */
    private String vendorcode;

    /**
     * 序列号
     */
    private String sn;

    /**
     * 图片MD5
     */
    private String imageMd5;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 图片状态：待清洗(0)，已清洗(1)，待审核(2)，已通过(3)，已拒绝(4)
     */
    private ImageStatus imageStatus;

    /**
     * 是否已锁定
     */
    private Boolean locked;

    /**
     * 质量评分
     */
    private Double qualityScore;

    /**
     * 清洗备注
     */
    private String cleanRemark;

    /**
     * 问题类型（逗号分隔）
     */
    private String problemTypes;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    // ========== 构造方法 ==========

    /**
     * 创建商品图片
     */
    public ProductImage(Long datasetId, String datasetCode, String shopcode, String vendorcode, String sn,
                       String imageMd5, String imageUrl) {
        if (datasetId == null) {
            throw new DomainException("数据集ID不能为空");
        }
        if (shopcode == null || shopcode.trim().isEmpty()) {
            throw new DomainException("店铺编码不能为空");
        }
        if (imageMd5 == null || imageMd5.trim().isEmpty()) {
            throw new DomainException("图片MD5不能为空");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new DomainException("图片URL不能为空");
        }

        this.datasetId = datasetId;
        this.datasetCode = datasetCode;
        this.shopcode = shopcode;
        this.vendorcode = vendorcode;
        this.sn = sn;
        this.imageMd5 = imageMd5;
        this.imageUrl = imageUrl;
        this.imageStatus = ImageStatus.PENDING_CLEANING;
        this.locked = false;
        this.qualityScore = null;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 从持久化对象重建（用于反序列化）
     */
    public static ProductImage rebuildFromPO(ProductImagePO po) {
        ProductImage image = new ProductImage();
        image.imageId = po.getImageId();
        image.datasetId = po.getDatasetId();
        image.datasetCode = po.getDatasetCode();
        image.categoryId = po.getCategoryId();
        image.categoryName = po.getCategoryName();
        image.pluCode = po.getPluCode();
        image.pluName = po.getPluName();
        image.shardId = po.getShardId();
        image.shopcode = po.getShopcode();
        image.vendorcode = po.getVendorcode();
        image.sn = po.getSn();
        image.imageMd5 = po.getImageMd5();
        image.imageUrl = po.getImageUrl();
        image.imageStatus = ImageStatus.valueOf(po.getImageStatus());
        image.locked = po.getLocked();
        image.qualityScore = po.getQualityScore();
        image.cleanRemark = po.getCleanRemark();
        image.problemTypes = po.getProblemTypes();
        image.createTime = po.getCreateTime();
        image.updateTime = po.getUpdateTime();
        return image;
    }

    // ========== 领域行为 (充血模型) ==========

    /**
     * 清洗图片
     */
    public void cleanImage(String problemTypes, String cleanRemark) {
        if (this.imageStatus != ImageStatus.PENDING_CLEANING) {
            throw new DomainException("图片状态不正确，只能从待清洗状态执行此操作");
        }
        this.imageStatus = ImageStatus.CLEANED;
        this.problemTypes = problemTypes;
        this.cleanRemark = cleanRemark;
        this.updateTime = new Date();
        addDomainEvent(new ImageCleanedEvent(this.imageId));
    }

    /**
     * 提交审核
     */
    public void submitForReview() {
        if (this.imageStatus != ImageStatus.CLEANED) {
            throw new DomainException("图片状态不正确，只能从已清洗状态执行此操作");
        }
        this.imageStatus = ImageStatus.PENDING_REVIEW;
        this.updateTime = new Date();
        addDomainEvent(new ImageSubmittedForReviewEvent(this.imageId));
    }

    /**
     * 审核通过
     */
    public void approve() {
        if (this.imageStatus != ImageStatus.PENDING_REVIEW) {
            throw new DomainException("图片状态不正确，只能从待审核状态执行此操作");
        }
        this.imageStatus = ImageStatus.APPROVED;
        this.updateTime = new Date();
        addDomainEvent(new ImageApprovedEvent(this.imageId));
    }

    /**
     * 审核拒绝
     */
    public void reject(String rejectReason) {
        if (this.imageStatus != ImageStatus.PENDING_REVIEW) {
            throw new DomainException("图片状态不正确，只能从待审核状态执行此操作");
        }
        this.imageStatus = ImageStatus.REJECTED;
        this.updateTime = new Date();
        addDomainEvent(new ImageRejectedEvent(this.imageId, rejectReason));
    }

    /**
     * 锁定图片
     */
    public void lock() {
        this.locked = true;
        this.updateTime = new Date();
    }

    /**
     * 解锁图片
     */
    public void unlock() {
        this.locked = false;
        this.updateTime = new Date();
    }

    /**
     * 设置质量评分
     */
    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
        this.updateTime = new Date();
    }

    /**
     * 检查图片是否已清洗
     */
    public boolean isCleaned() {
        return this.imageStatus == ImageStatus.CLEANED;
    }

    /**
     * 检查图片是否待审核
     */
    public boolean isPendingReview() {
        return this.imageStatus == ImageStatus.PENDING_REVIEW;
    }

    /**
     * 检查图片是否已通过审核
     */
    public boolean isApproved() {
        return this.imageStatus == ImageStatus.APPROVED;
    }

    /**
     * 检查图片是否被拒绝
     */
    public boolean isRejected() {
        return this.imageStatus == ImageStatus.REJECTED;
    }

    /**
     * 检查图片是否待清洗
     */
    public boolean isPendingCleaning() {
        return this.imageStatus == ImageStatus.PENDING_CLEANING;
    }

    /**
     * 检查图片是否已锁定
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * 检查图片是否处于活动状态
     */
    public boolean isActive() {
        return this.imageStatus == ImageStatus.PENDING_CLEANING
            || this.imageStatus == ImageStatus.CLEANED
            || this.imageStatus == ImageStatus.PENDING_REVIEW;
    }

    // ========== 领域服务 (领域逻辑) ==========

    /**
     * 验证图片编码格式
     */
    public static boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        // 基本的URL格式验证
        return url.startsWith("http://") || url.startsWith("https://");
    }

    // ========== 领域事件 ==========

    private Set<DomainEvent> domainEvents = new HashSet<>();

    public void addDomainEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    public Set<DomainEvent> pullDomainEvents() {
        Set<DomainEvent> events = new HashSet<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // ========== 枚举类 ==========

    /**
     * 图片状态枚举
     */
    public enum ImageStatus {
        PENDING_CLEANING(0, "待清洗"),
        CLEANED(1, "已清洗"),
        PENDING_REVIEW(2, "待审核"),
        APPROVED(3, "已通过"),
        REJECTED(4, "已拒绝");

        private final int value;
        private final String description;

        ImageStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static ImageStatus fromValue(int value) {
            for (ImageStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new DomainException("无效的图片状态值: " + value);
        }
    }
}

/**
 * 领域异常
 */
class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}

/**
 * 领域事件接口
 */
interface DomainEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}

/**
 * 图片已清洗事件
 */
class ImageCleanedEvent implements DomainEvent {
    private final Long imageId;
    private final Date eventTime;

    public ImageCleanedEvent(Long imageId) {
        this.imageId = imageId;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageCleaned";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }
}

/**
 * 图片已提交审核事件
 */
class ImageSubmittedForReviewEvent implements DomainEvent {
    private final Long imageId;
    private final Date eventTime;

    public ImageSubmittedForReviewEvent(Long imageId) {
        this.imageId = imageId;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageSubmittedForReview";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }
}

/**
 * 图片已通过审核事件
 */
class ImageApprovedEvent implements DomainEvent {
    private final Long imageId;
    private final Date eventTime;

    public ImageApprovedEvent(Long imageId) {
        this.imageId = imageId;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageApproved";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }
}

/**
 * 图片已拒绝事件
 */
class ImageRejectedEvent implements DomainEvent {
    private final Long imageId;
    private final String rejectReason;
    private final Date eventTime;

    public ImageRejectedEvent(Long imageId, String rejectReason) {
        this.imageId = imageId;
        this.rejectReason = rejectReason;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageRejected";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }

    public String getRejectReason() {
        return rejectReason;
    }
}
