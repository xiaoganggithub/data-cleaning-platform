package com.ruoyi.system.domain.event;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签领域事件基类
 */
public interface TagEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}
