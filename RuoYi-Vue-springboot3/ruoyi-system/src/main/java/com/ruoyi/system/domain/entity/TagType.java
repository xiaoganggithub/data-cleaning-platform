package com.ruoyi.system.domain.entity;

import com.ruoyi.system.domain.entity.DomainException;

/**
 * 标签类型枚举
 */
public enum TagType {
    QUALITY(0, "质量类"),
    CLARITY(1, "清晰度类"),
    BLUR(2, "模糊类"),
    OCCLUSION(3, "遮挡类"),
    BACKGROUND(4, "背景类"),
    OTHER(9, "其他");

    private final int value;
    private final String description;

    TagType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static TagType fromValue(int value) {
        for (TagType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new DomainException("无效的标签类型值: " + value);
    }
}
