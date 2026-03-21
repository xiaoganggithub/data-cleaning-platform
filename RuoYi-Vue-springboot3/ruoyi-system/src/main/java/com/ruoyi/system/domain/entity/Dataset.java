package com.ruoyi.system.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.ruoyi.system.domain.event.*;
import com.ruoyi.system.domain.valueobject.DatasetCode;
import com.ruoyi.system.domain.valueobject.ImageCount;
import com.ruoyi.system.domain.valueobject.QualityGrade;
import com.ruoyi.system.persistence.po.DatasetPO;

/**
 * 数据集领域实体 (充血模型)
 * 作为聚合根，管理Category、Shard、Image等实体
 */
@Data
public class Dataset implements Serializable {

    /**
     * 领域标识
     */
    private Long datasetId;

    /**
     * 数据集编码（值对象）
     */
    private DatasetCode datasetCode;

    /**
     * 数据集名称
     */
    private String name;

    /**
     * 数据集描述
     */
    private String description;

    /**
     * 状态：初始化(0)，待清洗(1)，清洗中(2)，已审核(3)，已发布(4)，已归档(5)，已删除(6)
     */
    private DatasetStatus status;

    /**
     * 总的图片数量（值对象）
     */
    private ImageCount totalImageCount;

    /**
     * 已清洗图片数量（值对象）
     */
    private ImageCount cleanedImageCount;

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
     * 默认构造器（用于反序列化）
     */
    public Dataset() {
    }

    /**
     * 创建数据集
     */
    public Dataset(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("数据集名称不能为空");
        }
        if (name.length() > 255) {
            throw new DomainException("数据集名称长度不能超过255个字符");
        }

        this.datasetCode = new DatasetCode(UUID.randomUUID().toString().replace("-", "").substring(0, 6));
        this.name = name;
        this.description = description;
        this.status = DatasetStatus.INITIALIZED;
        this.totalImageCount = new ImageCount(0);
        this.cleanedImageCount = new ImageCount(0);
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 从持久化对象重建（用于反序列化）
     */
    public static Dataset rebuildFromPO(DatasetPO po) {
        Dataset dataset = new Dataset();
        dataset.datasetId = po.getDatasetId();
        dataset.datasetCode = new DatasetCode(po.getDatasetCode());
        dataset.name = po.getName();
        dataset.description = po.getDescription();
        dataset.status = DatasetStatus.fromValue(po.getStatus());
        dataset.totalImageCount = new ImageCount(po.getTotalImageCount());
        dataset.cleanedImageCount = new ImageCount(po.getCleanedImageCount());
        dataset.createTime = po.getCreateTime();
        dataset.updateTime = po.getUpdateTime();
        return dataset;
    }

    // ========== 领域行为 (充血模型) ==========

    /**
     * 初始化数据集状态
     */
    public void initialize() {
        if (this.status != DatasetStatus.INITIALIZED) {
            throw new DomainException("数据集状态不正确，只能从初始化状态执行此操作");
        }
        this.status = DatasetStatus.PENDING_CLEANING;
        this.updateTime = new Date();
        addDomainEvent(new DatasetInitializedEvent(this.datasetCode.getValue()));
    }

    /**
     * 开始数据清洗
     */
    public void startCleaning() {
        if (this.status != DatasetStatus.PENDING_CLEANING) {
            throw new DomainException("数据集状态不正确，只能从待清洗状态执行此操作");
        }
        if (this.totalImageCount.getValue() == 0) {
            throw new DomainException("数据集没有图片，无法开始清洗");
        }
        this.status = DatasetStatus.CLEANING;
        this.updateTime = new Date();
        addDomainEvent(new DatasetStartedCleaningEvent(this.datasetCode.getValue()));
    }

    /**
     * 完成数据清洗
     */
    public void completeCleaning() {
        if (this.status != DatasetStatus.CLEANING) {
            throw new DomainException("数据集状态不正确，只能从清洗中状态执行此操作");
        }
        this.status = DatasetStatus.APPROVED;
        this.updateTime = new Date();
        addDomainEvent(new DatasetCompletedCleaningEvent(this.datasetCode.getValue(), this.cleanedImageCount.getValue()));
    }

    /**
     * 审核通过
     */
    public void approve() {
        if (this.status != DatasetStatus.APPROVED) {
            throw new DomainException("数据集状态不正确，只能从已审核状态执行此操作");
        }
        this.status = DatasetStatus.PUBLISHED;
        this.updateTime = new Date();
        addDomainEvent(new DatasetApprovedEvent(this.datasetCode.getValue()));
    }

    /**
     * 发布数据集
     */
    public void publish() {
        if (this.status != DatasetStatus.APPROVED) {
            throw new DomainException("数据集状态不正确，只能从已审核状态执行此操作");
        }
        this.status = DatasetStatus.PUBLISHED;
        this.updateTime = new Date();
        addDomainEvent(new DatasetApprovedEvent(this.datasetCode.getValue()));
    }

    /**
     * 归档数据集
     */
    public void archive() {
        if (this.status != DatasetStatus.PUBLISHED) {
            throw new DomainException("数据集状态不正确，只能从已发布状态执行此操作");
        }
        this.status = DatasetStatus.ARCHIVED;
        this.updateTime = new Date();
        addDomainEvent(new DatasetArchivedEvent(this.datasetCode.getValue()));
    }

    /**
     * 删除数据集
     */
    public void delete() {
        if (this.status == DatasetStatus.DELETED) {
            throw new DomainException("数据集已被删除");
        }
        if (this.status != DatasetStatus.ARCHIVED) {
            throw new DomainException("数据集必须先归档才能删除");
        }
        this.status = DatasetStatus.DELETED;
        this.updateTime = new Date();
        addDomainEvent(new DatasetDeletedEvent(this.datasetCode.getValue()));
    }

    /**
     * 增加总图片数
     */
    public void addTotalImageCount(int count) {
        this.totalImageCount = new ImageCount(this.totalImageCount.getValue() + count);
        this.updateTime = new Date();
    }

    /**
     * 增加已清洗图片数
     */
    public void addCleanedImageCount(int count) {
        this.cleanedImageCount = new ImageCount(this.cleanedImageCount.getValue() + count);
        this.updateTime = new Date();
    }

    /**
     * 更新统计信息
     */
    public void updateStatistics() {
        this.updateTime = new Date();
        addDomainEvent(new DatasetStatisticsUpdatedEvent(this.datasetCode.getValue(), this.totalImageCount.getValue(), this.cleanedImageCount.getValue()));
    }

    /**
     * 检查数据集是否处于活动状态
     */
    public boolean isActive() {
        return this.status == DatasetStatus.PENDING_CLEANING
            || this.status == DatasetStatus.CLEANING
            || this.status == DatasetStatus.APPROVED
            || this.status == DatasetStatus.PUBLISHED;
    }

    // ========== 领域服务 (领域逻辑) ==========

    /**
     * 生成数据集名称
     */
    public static String generateDatasetName(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new DomainException("前缀不能为空");
        }
        if (prefix.length() > 20) {
            throw new DomainException("前缀长度不能超过20个字符");
        }
        return prefix + "-" + System.currentTimeMillis();
    }

    /**
     * 验证数据集编码格式
     */
    public static boolean isValidDatasetCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        // 格式：XXXXXX（6位大写字母和数字）
        return code.matches("^[A-Z0-9]{6}$");
    }

    /**
     * 计算数据集质量评分
     */
    public double calculateQualityScore() {
        if (this.totalImageCount.getValue() == 0) {
            return 0.0;
        }
        double rate = (double) this.cleanedImageCount.getValue() / this.totalImageCount.getValue();
        return rate * 100;
    }

    /**
     * 获取质量等级
     */
    public QualityGrade getQualityGrade() {
        double score = calculateQualityScore();
        if (score >= 90) return QualityGrade.A;
        if (score >= 80) return QualityGrade.B;
        if (score >= 70) return QualityGrade.C;
        if (score >= 60) return QualityGrade.D;
        return QualityGrade.E;
    }

    /**
     * 检查是否已完成清洗
     */
    public boolean isCleaningCompleted() {
        return this.status == DatasetStatus.APPROVED
            || this.status == DatasetStatus.PUBLISHED;
    }

    /**
     * 检查是否可以删除
     */
    public boolean isDeletable() {
        return this.status == DatasetStatus.ARCHIVED;
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
}
