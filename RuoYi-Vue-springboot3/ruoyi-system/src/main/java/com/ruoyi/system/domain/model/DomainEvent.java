package com.ruoyi.system.domain.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 领域事件接口
 * 所有领域事件都实现此接口
 */
public interface DomainEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}
