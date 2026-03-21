package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 图片已拒绝事件
 */
public class ImageRejectedEvent implements DomainEvent {
    private final Long imageId;
    private final String rejectReason;
    private final Date eventTime;

    public ImageRejectedEvent(Long imageId, String rejectReason) {
        this.imageId = imageId;
        this.rejectReason = rejectReason;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageRejected";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }

    public String getRejectReason() {
        return rejectReason;
    }
}
