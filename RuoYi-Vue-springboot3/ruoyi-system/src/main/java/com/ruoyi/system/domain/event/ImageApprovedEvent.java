package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 图片已通过审核事件
 */
public class ImageApprovedEvent implements DomainEvent {
    private final Long imageId;
    private final Date eventTime;

    public ImageApprovedEvent(Long imageId) {
        this.imageId = imageId;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageApproved";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }
}
