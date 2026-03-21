package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 数据集开始清洗事件
 */
public class DatasetStartedCleaningEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetStartedCleaningEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetStartedCleaning";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }
}
