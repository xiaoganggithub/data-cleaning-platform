package com.ruoyi.system.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

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
        dataset.status = DatasetStatus.valueOf(po.getStatus());
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
        addDomainEvent(new DatasetInitializedEvent(this.datasetCode));
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
        addDomainEvent(new DatasetStartedCleaningEvent(this.datasetCode));
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
        addDomainEvent(new DatasetCompletedCleaningEvent(this.datasetCode, this.cleanedImageCount.getValue()));
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
        addDomainEvent(new DatasetApprovedEvent(this.datasetCode));
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
        addDomainEvent(new DatasetArchivedEvent(this.datasetCode));
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
        addDomainEvent(new DatasetDeletedEvent(this.datasetCode));
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
        addDomainEvent(new DatasetStatisticsUpdatedEvent(this.datasetCode, this.totalImageCount.getValue(), this.cleanedImageCount.getValue()));
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

    // ========== 枚举类 ==========

    /**
     * 数据集状态枚举
     */
    public enum DatasetStatus {
        INITIALIZED(0, "初始化"),
        PENDING_CLEANING(1, "待清洗"),
        CLEANING(2, "清洗中"),
        APPROVED(3, "已审核"),
        PUBLISHED(4, "已发布"),
        ARCHIVED(5, "已归档"),
        DELETED(6, "已删除");

        private final int value;
        private final String description;

        DatasetStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static DatasetStatus fromValue(int value) {
            for (DatasetStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new DomainException("无效的数据集状态值: " + value);
        }
    }

    /**
     * 质量等级枚举
     */
    public enum QualityGrade {
        A(90, 100, "优秀"),
        B(80, 89, "良好"),
        C(70, 79, "一般"),
        D(60, 69, "较差"),
        E(0, 59, "不合格");

        private final int min;
        private final int max;
        private final String description;

        QualityGrade(int min, int max, String description) {
            this.min = min;
            this.max = max;
            this.description = description;
        }

        public boolean contains(int score) {
            return score >= min && score <= max;
        }

        public String getDescription() {
            return description;
        }
    }
}

/**
 * 数据集编码值对象
 */
@Data
public class DatasetCode {
    private final String value;

    public DatasetCode(String value) {
        if (value == null || value.isEmpty()) {
            throw new DomainException("数据集编码不能为空");
        }
        if (value.length() != 6) {
            throw new DomainException("数据集编码长度必须为6位");
        }
        this.value = value.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatasetCode that = (DatasetCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

/**
 * 图片数量值对象
 */
@Data
public class ImageCount {
    private final int value;

    public ImageCount(int value) {
        if (value < 0) {
            throw new DomainException("图片数量不能为负数");
        }
        this.value = value;
    }

    public void increment() {
        this.value++;
    }

    public void decrement() {
        if (this.value > 0) {
            this.value--;
        }
    }

    public boolean isEmpty() {
        return this.value == 0;
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
 * 数据集已初始化事件
 */
class DatasetInitializedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetInitializedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetInitialized";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }
}

/**
 * 数据集开始清洗事件
 */
class DatasetStartedCleaningEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetStartedCleaningEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetStartedCleaning";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }
}

/**
 * 数据集完成清洗事件
 */
class DatasetCompletedCleaningEvent implements DomainEvent {
    private final String datasetCode;
    private final int cleanedCount;
    private final Date eventTime;

    public DatasetCompletedCleaningEvent(String datasetCode, int cleanedCount) {
        this.datasetCode = datasetCode;
        this.cleanedCount = cleanedCount;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetCompletedCleaning";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }

    public int getCleanedCount() {
        return cleanedCount;
    }
}

/**
 * 数据集已审核事件
 */
class DatasetApprovedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetApprovedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetApproved";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }
}

/**
 * 数据集已归档事件
 */
class DatasetArchivedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetArchivedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetArchived";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }
}

/**
 * 数据集已删除事件
 */
class DatasetDeletedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetDeletedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetDeleted";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }
}

/**
 * 数据集统计更新事件
 */
class DatasetStatisticsUpdatedEvent implements DomainEvent {
    private final String datasetCode;
    private final int totalImages;
    private final int cleanedImages;
    private final Date eventTime;

    public DatasetStatisticsUpdatedEvent(String datasetCode, int totalImages, int cleanedImages) {
        this.datasetCode = datasetCode;
        this.totalImages = totalImages;
        this.cleanedImages = cleanedImages;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetStatisticsUpdated";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }
}
