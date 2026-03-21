package com.ruoyi.system.domain.event;

import com.ruoyi.system.domain.valueobject.ShardCode;
import java.util.Date;

/**
 * 分片处理失败事件
 */
public class ShardFailedEvent implements DomainEvent {
    private final ShardCode shardCode;
    private final String errorMessage;
    private final Date eventTime;

    public ShardFailedEvent(ShardCode shardCode, String errorMessage) {
        this.shardCode = shardCode;
        this.errorMessage = errorMessage;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ShardFailed";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public ShardCode getShardCode() {
        return shardCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
