package com.ruoyi.system.domain.event;

import com.ruoyi.system.domain.valueobject.ShardCode;
import java.util.Date;

/**
 * 分片处理完成事件
 */
public class ShardCompletedEvent implements DomainEvent {
    private final ShardCode shardCode;
    private final int processedCount;
    private final int errorCount;
    private final Date eventTime;

    public ShardCompletedEvent(ShardCode shardCode, int processedCount, int errorCount) {
        this.shardCode = shardCode;
        this.processedCount = processedCount;
        this.errorCount = errorCount;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ShardCompleted";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public ShardCode getShardCode() {
        return shardCode;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }
}
