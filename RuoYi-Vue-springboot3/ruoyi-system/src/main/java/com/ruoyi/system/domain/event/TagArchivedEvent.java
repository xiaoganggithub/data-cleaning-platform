package com.ruoyi.system.domain.event;

import com.ruoyi.system.domain.valueobject.TagCode;
import java.util.Date;

/**
 * 标签已归档事件
 */
public class TagArchivedEvent implements DomainEvent {
    private final TagCode tagCode;
    private final Date eventTime;

    public TagArchivedEvent(TagCode tagCode) {
        this.tagCode = tagCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "TagArchived";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public TagCode getTagCode() {
        return tagCode;
    }
}
