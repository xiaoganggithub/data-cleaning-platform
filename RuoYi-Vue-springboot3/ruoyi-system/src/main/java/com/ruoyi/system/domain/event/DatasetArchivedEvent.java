package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 数据集已归档事件
 */
public class DatasetArchivedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetArchivedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetArchived";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }
}
