package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 数据集已审核事件
 */
public class DatasetApprovedEvent implements DomainEvent {
    private final String datasetCode;
    private final Date eventTime;

    public DatasetApprovedEvent(String datasetCode) {
        this.datasetCode = datasetCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetApproved";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }
}
