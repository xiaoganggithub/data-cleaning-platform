package com.ruoyi.system.domain.event;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据集领域事件基类
 */
public interface DatasetEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}
