package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.entity.DomainException;

/**
 * 图片状态枚举
 */
public enum ImageStatus {
    PENDING_CLEANING(0, "待清洗"),
    CLEANED(1, "已清洗"),
    PENDING_REVIEW(2, "待审核"),
    APPROVED(3, "已通过"),
    REJECTED(4, "已拒绝");

    private final int value;
    private final String description;

    ImageStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ImageStatus fromValue(int value) {
        for (ImageStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new DomainException("无效的图片状态值: " + value);
    }
}
