package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 数据集统计更新事件
 */
public class DatasetStatisticsUpdatedEvent implements DomainEvent {
    private final String datasetCode;
    private final int totalImages;
    private final int cleanedImages;
    private final Date eventTime;

    public DatasetStatisticsUpdatedEvent(String datasetCode, int totalImages, int cleanedImages) {
        this.datasetCode = datasetCode;
        this.totalImages = totalImages;
        this.cleanedImages = cleanedImages;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "DatasetStatisticsUpdated";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public String getDatasetCode() {
        return datasetCode;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public int getCleanedImages() {
        return cleanedImages;
    }
}
