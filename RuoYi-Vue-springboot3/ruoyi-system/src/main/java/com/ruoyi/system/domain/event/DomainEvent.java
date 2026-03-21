package com.ruoyi.system.domain.event;

import java.io.Serializable;
import java.util.Date;

/**
 * 领域事件接口
 */
public interface DomainEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}
