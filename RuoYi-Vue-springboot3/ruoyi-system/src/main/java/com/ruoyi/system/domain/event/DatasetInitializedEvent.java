package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 数据集已初始化事件
 */
public class DatasetInitializedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetInitializedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetInitialized";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }
}
