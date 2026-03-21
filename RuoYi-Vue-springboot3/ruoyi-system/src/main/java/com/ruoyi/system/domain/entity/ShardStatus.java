package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.entity.DomainException;

/**
 * 分片状态枚举
 */
public enum ShardStatus {
    PENDING(0, "准备中"),
    PROCESSING(1, "处理中"),
    COMPLETED(2, "已完成"),
    FAILED(3, "失败");

    private final int value;
    private final String description;

    ShardStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ShardStatus fromValue(int value) {
        for (ShardStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new DomainException("无效的分片状态值: " + value);
    }
}
