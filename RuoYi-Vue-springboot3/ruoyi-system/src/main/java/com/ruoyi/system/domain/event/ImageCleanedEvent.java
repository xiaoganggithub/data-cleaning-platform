package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 图片已清洗事件
 */
public class ImageCleanedEvent implements DomainEvent {
    private final Long imageId;
    private final Date eventTime;

    public ImageCleanedEvent(Long imageId) {
        this.imageId = imageId;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageCleaned";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }
}
