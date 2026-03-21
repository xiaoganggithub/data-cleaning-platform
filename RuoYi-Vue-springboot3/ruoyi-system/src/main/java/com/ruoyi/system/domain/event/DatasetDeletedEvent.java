package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 数据集已删除事件
 */
public class DatasetDeletedEvent implements DomainEvent {
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

    public String getDatasetCode() {
        return datasetCode;
    }
}
