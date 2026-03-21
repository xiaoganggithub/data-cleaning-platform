package com.ruoyi.system.domain.event;

import java.util.Date;

/**
 * 图片已提交审核事件
 */
public class ImageSubmittedForReviewEvent implements DomainEvent {
    private final Long imageId;
    private final Date eventTime;

    public ImageSubmittedForReviewEvent(Long imageId) {
        this.imageId = imageId;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "ImageSubmittedForReview";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Long getImageId() {
        return imageId;
    }
}
