package com.ruoyi.system.domain.valueobject;

/**
 * 质量等级枚举
 */
public enum QualityGrade {
    A(90, 100, "优秀", 1),
    B(80, 89, "良好", 2),
    C(70, 79, "一般", 3),
    D(60, 69, "较差", 4),
    E(0, 59, "不合格", 5);

    private final int min;
    private final int max;
    private final String description;
    private final int level;

    QualityGrade(int min, int max, String description, int level) {
        this.min = min;
        this.max = max;
        this.description = description;
        this.level = level;
    }

    public boolean contains(int score) {
        return score >= min && score <= max;
    }

    public String getDescription() {
        return description;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 根据等级数字获取质量等级
     * 1 -> A, 2 -> B, 3 -> C, 4 -> D, 5 -> E
     */
    public static QualityGrade of(int level) {
        for (QualityGrade grade : values()) {
            if (grade.level == level) {
                return grade;
            }
        }
        throw new IllegalArgumentException("无效的质量等级: " + level);
    }
}
