package com.ruoyi.system.domain.event;

import com.ruoyi.system.domain.valueobject.CategoryCode;
import java.util.Date;

/**
 * 分类已归档事件
 */
public class CategoryArchivedEvent implements DomainEvent {
    private final CategoryCode categoryCode;
    private final Date eventTime;

    public CategoryArchivedEvent(CategoryCode categoryCode) {
        this.categoryCode = categoryCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "CategoryArchived";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public CategoryCode getCategoryCode() {
        return categoryCode;
    }
}
