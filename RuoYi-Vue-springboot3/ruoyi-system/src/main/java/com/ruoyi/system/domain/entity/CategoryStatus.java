package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.entity.DomainException;

/**
 * 分类状态枚举
 */
public enum CategoryStatus {
    NORMAL(0, "正常"),
    ARCHIVED(1, "已归档");

    private final int value;
    private final String description;

    CategoryStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static CategoryStatus fromValue(int value) {
        for (CategoryStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new DomainException("无效的分类状态值: " + value);
    }
}
