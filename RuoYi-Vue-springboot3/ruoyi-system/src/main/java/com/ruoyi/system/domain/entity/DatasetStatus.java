package com.ruoyi.system.domain.entity;

/**
 * 数据集状态枚举
 */
public enum DatasetStatus {
    INITIALIZED(0, "初始化"),
    PENDING_CLEANING(1, "待清洗"),
    CLEANING(2, "清洗中"),
    APPROVED(3, "已审核"),
    PUBLISHED(4, "已发布"),
    ARCHIVED(5, "已归档"),
    DELETED(6, "已删除");

    private final int value;
    private final String description;

    DatasetStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static DatasetStatus fromValue(int value) {
        for (DatasetStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new DomainException("无效的数据集状态值: " + value);
    }
}
