package com.ruoyi.system.domain.valueobject;

import lombok.Data;

/**
 * 质量等级值对象
 * 用于封装数据集质量评分等级
 */
@Data
public class QualityGrade {
    /**
     * 质量等级值
     */
    private final int value;

    /**
     * 最小分值
     */
    private final int min;

    /**
     * 最大分值
     */
    private final int max;

    /**
     * 等级描述
     */
    private final String description;

    private QualityGrade(int value, int min, int max, String description) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.description = description;
    }

    /**
     * 创建质量等级
     */
    public static QualityGrade of(int score) {
        if (score >= 90) {
            return new QualityGrade(1, 90, 100, "优秀");
        } else if (score >= 80) {
            return new QualityGrade(2, 80, 89, "良好");
        } else if (score >= 70) {
            return new QualityGrade(3, 70, 79, "一般");
        } else if (score >= 60) {
            return new QualityGrade(4, 60, 69, "较差");
        } else {
            return new QualityGrade(5, 0, 59, "不合格");
        }
    }

    /**
     * 获取等级描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 检查分数是否属于此等级
     */
    public boolean contains(int score) {
        return score >= min && score <= max;
    }

    /**
     * 获取等级值
     */
    public int getValue() {
        return value;
    }
}
