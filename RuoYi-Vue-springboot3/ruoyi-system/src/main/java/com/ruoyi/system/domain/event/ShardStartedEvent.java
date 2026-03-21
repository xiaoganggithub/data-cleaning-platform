package com.ruoyi.system.domain.event;

import com.ruoyi.system.domain.valueobject.ShardCode;
import java.util.Date;

/**
 * 分片开始处理事件
 */
public class ShardStartedEvent implements DomainEvent {
    private final ShardCode shardCode;
    private final Date eventTime;

    public ShardStartedEvent(ShardCode shardCode) {
        this.shardCode = shardCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ShardStarted";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public ShardCode getShardCode() {
        return shardCode;
    }
}
