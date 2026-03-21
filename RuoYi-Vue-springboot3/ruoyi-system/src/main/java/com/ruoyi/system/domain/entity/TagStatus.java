package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.entity.DomainException;

/**
 * 标签状态枚举
 */
public enum TagStatus {
    NORMAL(0, "正常"),
    ARCHIVED(1, "已归档");

    private final int value;
    private final String description;

    TagStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static TagStatus fromValue(int value) {
        for (TagStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new DomainException("无效的标签状态值: " + value);
    }
}
