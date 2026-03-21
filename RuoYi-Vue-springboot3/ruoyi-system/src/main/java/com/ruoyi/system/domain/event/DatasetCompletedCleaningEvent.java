package com.ruoyi.system.domain.event;

import lombok.Getter;

import java.util.Date;

/**
 * 数据集完成清洗事件
 */
public class DatasetCompletedCleaningEvent implements DomainEvent {
    @Getter
    private final String datasetCode;
    @Getter
    private final int cleanedCount;
    private final Date eventTime;

    public DatasetCompletedCleaningEvent(String datasetCode, int cleanedCount) {
        this.datasetCode = datasetCode;
        this.cleanedCount = cleanedCount;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetCompletedCleaning";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

}
