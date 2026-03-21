package com.ruoyi.system.domain.service;

import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.valueobject.QualityGrade;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 数据集领域服务
 * 提供数据集相关的业务逻辑和规则
 */
public class DatasetDomainService {

    /**
     * 验证数据集状态转换
     */
    public static void validateStatusTransition(DatasetStatus currentStatus, DatasetStatus targetStatus) {
        // 初始化 → 待清洗
        if (currentStatus == DatasetStatus.INITIALIZED && targetStatus == DatasetStatus.PENDING_CLEANING) {
            return;
        }

        // 待清洗 → 清洗中
        if (currentStatus == DatasetStatus.PENDING_CLEANING && targetStatus == DatasetStatus.CLEANING) {
            return;
        }

        // 清洗中 → 已审核
        if (currentStatus == DatasetStatus.CLEANING && targetStatus == DatasetStatus.APPROVED) {
            return;
        }

        // 已审核 → 已发布
        if (currentStatus == DatasetStatus.APPROVED && targetStatus == DatasetStatus.PUBLISHED) {
            return;
        }

        // 已发布 → 已归档
        if (currentStatus == DatasetStatus.PUBLISHED && targetStatus == DatasetStatus.ARCHIVED) {
            return;
        }

        throw new IllegalArgumentException("不允许的状态转换: " + currentStatus + " -> " + targetStatus);
    }

    /**
     * 计算数据集质量评分
     */
    public static double calculateQualityScore(int totalImageCount, int cleanedImageCount) {
        if (totalImageCount == 0) {
            return 0.0;
        }
        double rate = (double) cleanedImageCount / totalImageCount;
        return rate * 100;
    }

    /**
     * 根据评分获取质量等级
     */
    public static QualityGrade getQualityGrade(double score) {
        if (score >= 90) return QualityGrade.of(1);
        if (score >= 80) return QualityGrade.of(2);
        if (score >= 70) return QualityGrade.of(3);
        if (score >= 60) return QualityGrade.of(4);
        return QualityGrade.of(5);
    }

    /**
     * 验证数据集是否可以开始清洗
     */
    public static boolean canStartCleaning(DatasetStatus status, int totalImageCount) {
        return status == DatasetStatus.PENDING_CLEANING && totalImageCount > 0;
    }

    /**
     * 验证数据集是否可以完成清洗
     */
    public static boolean canCompleteCleaning(DatasetStatus status, int cleanedImageCount, int totalImageCount) {
        return status == DatasetStatus.CLEANING
            && cleanedImageCount > 0
            && cleanedImageCount == totalImageCount;
    }

    /**
     * 验证数据集是否可以审核
     */
    public static boolean canApprove(DatasetStatus status) {
        return status == DatasetStatus.APPROVED;
    }

    /**
     * 验证数据集是否可以归档
     */
    public static boolean canArchive(DatasetStatus status) {
        return status == DatasetStatus.PUBLISHED;
    }

    /**
     * 验证数据集是否可以删除
     */
    public static boolean canDelete(DatasetStatus status) {
        return status == DatasetStatus.ARCHIVED;
    }

    /**
     * 检查数据集是否已完成清洗
     */
    public static boolean isCleaningCompleted(DatasetStatus status) {
        return status == DatasetStatus.APPROVED || status == DatasetStatus.PUBLISHED;
    }

    /**
     * 检查数据集是否可以删除
     */
    public static boolean isDeletable(DatasetStatus status) {
        return status == DatasetStatus.ARCHIVED;
    }

    /**
     * 检查数据集是否处于活动状态
     */
    public static boolean isActive(DatasetStatus status) {
        return status == DatasetStatus.PENDING_CLEANING
            || status == DatasetStatus.CLEANING
            || status == DatasetStatus.APPROVED
            || status == DatasetStatus.PUBLISHED;
    }

    /**
     * 生成数据集名称
     */
    public static String generateDatasetName(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("前缀不能为空");
        }
        if (prefix.length() > 20) {
            throw new IllegalArgumentException("前缀长度不能超过20个字符");
        }
        return prefix + "-" + System.currentTimeMillis();
    }

    /**
     * 验证数据集编码格式
     */
    public static boolean isValidDatasetCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return code.matches("^[A-Z0-9]{6}$");
    }

    /**
     * 验证数据集名称
     */
    public static void validateDatasetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("数据集名称不能为空");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("数据集名称长度不能超过255个字符");
        }
    }

    /**
     * 验证数据集描述
     */
    public static void validateDatasetDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("数据集描述长度不能超过1000个字符");
        }
    }

    /**
     * 生成清洗完成通知消息
     */
    public static String generateCompletionNotification(int totalImages, int cleanedImages, double score) {
        return String.format("数据集清洗完成！\n总图片数: %d\n已清洗: %d\n清洗率: %.2f%%\n质量评分: %.2f分",
            totalImages, cleanedImages, (cleanedImages * 100.0 / totalImages), score);
    }

    /**
     * 生成审核通过通知消息
     */
    public static String generateApprovedNotification(int totalImages, int cleanedImages) {
        double rate = totalImages > 0 ? (cleanedImages * 100.0 / totalImages) : 0;
        return String.format("数据集审核通过！\n总图片数: %d\n已清洗: %d\n清洗率: %.2f%%",
            totalImages, cleanedImages, rate);
    }
}
