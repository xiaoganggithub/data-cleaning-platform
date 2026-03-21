package com.ruoyi.system.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.ruoyi.system.domain.event.DomainEvent;
import com.ruoyi.system.domain.event.ShardCompletedEvent;
import com.ruoyi.system.domain.event.ShardFailedEvent;
import com.ruoyi.system.domain.event.ShardStartedEvent;
import com.ruoyi.system.domain.valueobject.ShardCode;
import com.ruoyi.system.persistence.po.DataShardPO;

/**
 * 数据分片领域实体 (充血模型)
 * 作为聚合根，管理DataShardInfo等实体
 */
@Data
public class DataShard implements Serializable {

    /**
     * 领域标识
     */
    private Long shardId;

    /**
     * 分片编码（值对象）
     */
    private ShardCode shardCode;

    /**
     * 数据集ID
     */
    private Long datasetId;

    /**
     * 数据集编码
     */
    private String datasetCode;

    /**
     * 分片名称
     */
    private String name;

    /**
     * 分片描述
     */
    private String description;

    /**
     * 状态：准备中(0)，处理中(1)，已完成(2)，失败(3)
     */
    private ShardStatus status;

    /**
     * 图像数量
     */
    private int imageCount;

    /**
     * 已处理图像数量
     */
    private int processedCount;

    /**
     * 错误数量
     */
    private int errorCount;

    /**
     * 进度百分比
     */
    private double progressPercent;

    /**
     * 总图像URL数量
     */
    private int totalUrlCount;

    /**
     * 成功导入URL数量
     */
    private int successImportCount;

    /**
     * 失败导入URL数量
     */
    private int failedImportCount;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 完成时间
     */
    private Date completeTime;

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
    public DataShard() {
    }

    /**
     * 创建数据分片
     */
    public DataShard(Long datasetId, String datasetCode, String name, String description) {
        if (datasetId == null) {
            throw new DomainException("数据集ID不能为空");
        }
        if (datasetCode == null || datasetCode.trim().isEmpty()) {
            throw new DomainException("数据集编码不能为空");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("分片名称不能为空");
        }

        this.shardCode = new ShardCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        this.datasetId = datasetId;
        this.datasetCode = datasetCode;
        this.name = name;
        this.description = description;
        this.status = ShardStatus.PENDING;
        this.imageCount = 0;
        this.processedCount = 0;
        this.errorCount = 0;
        this.progressPercent = 0.0;
        this.totalUrlCount = 0;
        this.successImportCount = 0;
        this.failedImportCount = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 从持久化对象重建（用于反序列化）
     */
    public static DataShard rebuildFromPO(DataShardPO po) {
        DataShard shard = new DataShard();
        shard.shardId = po.getShardId();
        shard.shardCode = new ShardCode(po.getShardCode());
        shard.datasetId = po.getDatasetId();
        shard.datasetCode = po.getDatasetCode();
        shard.name = po.getName();
        shard.description = po.getDescription();
        shard.status = ShardStatus.fromValue(po.getStatus());
        shard.imageCount = po.getImageCount();
        shard.processedCount = po.getProcessedCount();
        shard.errorCount = po.getErrorCount();
        shard.progressPercent = po.getProgressPercent();
        shard.totalUrlCount = po.getTotalUrlCount();
        shard.successImportCount = po.getSuccessImportCount();
        shard.failedImportCount = po.getFailedImportCount();
        shard.startTime = po.getStartTime();
        shard.completeTime = po.getCompleteTime();
        shard.createTime = po.getCreateTime();
        shard.updateTime = po.getUpdateTime();
        return shard;
    }

    // ========== 领域行为 (充血模型) ==========

    /**
     * 开始处理分片
     */
    public void startProcessing() {
        if (this.status != ShardStatus.PENDING) {
            throw new DomainException("分片状态不正确，只能从准备中状态执行此操作");
        }
        this.status = ShardStatus.PROCESSING;
        this.startTime = new Date();
        this.updateTime = new Date();
        addDomainEvent(new ShardStartedEvent(this.shardCode));
    }

    /**
     * 处理完成
     */
    public void processingComplete() {
        if (this.status != ShardStatus.PROCESSING) {
            throw new DomainException("分片状态不正确，只能从处理中状态执行此操作");
        }
        this.status = ShardStatus.COMPLETED;
        this.completeTime = new Date();
        this.updateTime = new Date();
        addDomainEvent(new ShardCompletedEvent(this.shardCode, this.processedCount, this.errorCount));
    }

    /**
     * 处理失败
     */
    public void processingFailed() {
        if (this.status != ShardStatus.PROCESSING) {
            throw new DomainException("分片状态不正确，只能从处理中状态执行此操作");
        }
        this.status = ShardStatus.FAILED;
        this.completeTime = new Date();
        this.updateTime = new Date();
        addDomainEvent(new ShardFailedEvent(this.shardCode, "处理过程中发生错误"));
    }

    /**
     * 增加图像数量
     */
    public void addImageCount(int count) {
        this.imageCount += count;
        this.updateTime = new Date();
    }

    /**
     * 增加已处理数量
     */
    public void addProcessedCount(int count) {
        this.processedCount += count;
        this.updateTime = new Date();
    }

    /**
     * 增加错误数量
     */
    public void addErrorCount(int count) {
        this.errorCount += count;
        this.updateTime = new Date();
    }

    /**
     * 更新进度百分比
     */
    public void updateProgressPercent(double percent) {
        if (percent < 0 || percent > 100) {
            throw new DomainException("进度百分比必须在0-100之间");
        }
        this.progressPercent = percent;
        this.updateTime = new Date();
    }

    /**
     * 增加总URL数量
     */
    public void addTotalUrlCount(int count) {
        this.totalUrlCount += count;
        this.updateTime = new Date();
    }

    /**
     * 增加成功导入URL数量
     */
    public void addSuccessImportCount(int count) {
        this.successImportCount += count;
        this.updateTime = new Date();
    }

    /**
     * 增加失败导入URL数量
     */
    public void addFailedImportCount(int count) {
        this.failedImportCount += count;
        this.updateTime = new Date();
    }

    /**
     * 检查分片是否完成
     */
    public boolean isCompleted() {
        return this.status == ShardStatus.COMPLETED;
    }

    /**
     * 检查分片是否正在处理
     */
    public boolean isProcessing() {
        return this.status == ShardStatus.PROCESSING;
    }

    /**
     * 检查分片是否可以重新处理
     */
    public boolean canRetry() {
        return this.status == ShardStatus.FAILED;
    }

    /**
     * 检查分片是否处于活动状态
     */
    public boolean isActive() {
        return this.status == ShardStatus.PENDING
            || this.status == ShardStatus.PROCESSING;
    }

    /**
     * 计算完成率
     */
    public double calculateCompletionRate() {
        if (this.imageCount == 0) {
            return 0.0;
        }
        return (double) this.processedCount / this.imageCount * 100;
    }

    // ========== 领域服务 (领域逻辑) ==========

    /**
     * 生成分片名称
     */
    public static String generateShardName(String datasetCode, int index) {
        if (datasetCode == null || datasetCode.trim().isEmpty()) {
            throw new DomainException("数据集编码不能为空");
        }
        return datasetCode + "-shard-" + index;
    }

    /**
     * 验证分片编码格式
     */
    public static boolean isValidShardCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        // 格式：XXXXXXXX（8位大写字母和数字）
        return code.matches("^[A-Z0-9]{8}$");
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
