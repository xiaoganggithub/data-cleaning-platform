package com.ruoyi.system.domain.event;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据分片领域事件基类
 */
public interface DataShardEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}
