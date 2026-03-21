package com.ruoyi.system.domain.service;

import com.ruoyi.system.domain.entity.ImageStatus;

/**
 * 图片领域服务
 * 提供图片相关的业务逻辑和规则
 */
public class ImageDomainService {

    /**
     * 验证图片URL
     */
    public static boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * 验证图片MD5
     */
    public static boolean isValidImageMd5(String md5) {
        if (md5 == null || md5.isEmpty()) {
            return false;
        }
        return md5.matches("^[a-fA-F0-9]{32}$");
    }

    /**
     * 验证图片名称
     */
    public static void validateImageName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("图片名称不能为空");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("图片名称长度不能超过255个字符");
        }
    }

    /**
     * 验证清洗备注
     */
    public static void validateCleanRemark(String remark) {
        if (remark != null && remark.length() > 500) {
            throw new IllegalArgumentException("清洗备注长度不能超过500个字符");
        }
    }

    /**
     * 验证问题类型
     */
    public static void validateProblemTypes(String problemTypes) {
        if (problemTypes != null && problemTypes.length() > 1000) {
            throw new IllegalArgumentException("问题类型长度不能超过1000个字符");
        }
    }

    /**
     * 验证质量评分
     */
    public static void validateQualityScore(Double qualityScore) {
        if (qualityScore != null && (qualityScore < 0 || qualityScore > 100)) {
            throw new IllegalArgumentException("质量评分必须在0-100之间");
        }
    }

    /**
     * 验证图片状态转换
     */
    public static void validateStatusTransition(ImageStatus currentStatus, ImageStatus targetStatus) {
        // 待清洗 → 已清洗
        if (currentStatus == ImageStatus.PENDING_CLEANING && targetStatus == ImageStatus.CLEANED) {
            return;
        }

        // 已清洗 → 待审核
        if (currentStatus == ImageStatus.CLEANED && targetStatus == ImageStatus.PENDING_REVIEW) {
            return;
        }

        // 待审核 → 已通过
        if (currentStatus == ImageStatus.PENDING_REVIEW && targetStatus == ImageStatus.APPROVED) {
            return;
        }

        // 待审核 → 已拒绝
        if (currentStatus == ImageStatus.PENDING_REVIEW && targetStatus == ImageStatus.REJECTED) {
            return;
        }

        throw new IllegalArgumentException("不允许的状态转换: " + currentStatus + " -> " + targetStatus);
    }

    /**
     * 检查图片是否已清洗
     */
    public static boolean isCleaned(ImageStatus status) {
        return status == ImageStatus.CLEANED;
    }

    /**
     * 检查图片是否待审核
     */
    public static boolean isPendingReview(ImageStatus status) {
        return status == ImageStatus.PENDING_REVIEW;
    }

    /**
     * 检查图片是否已通过审核
     */
    public static boolean isApproved(ImageStatus status) {
        return status == ImageStatus.APPROVED;
    }

    /**
     * 检查图片是否被拒绝
     */
    public static boolean isRejected(ImageStatus status) {
        return status == ImageStatus.REJECTED;
    }

    /**
     * 检查图片是否待清洗
     */
    public static boolean isPendingCleaning(ImageStatus status) {
        return status == ImageStatus.PENDING_CLEANING;
    }

    /**
     * 检查图片是否已锁定
     */
    public static boolean isLocked(Boolean locked) {
        return locked != null && locked;
    }

    /**
     * 检查图片是否处于活动状态
     */
    public static boolean isActive(ImageStatus status) {
        return status == ImageStatus.PENDING_CLEANING
            || status == ImageStatus.CLEANED
            || status == ImageStatus.PENDING_REVIEW;
    }

    /**
     * 生成图片清理备注
     */
    public static String generateCleanRemark(String problemTypes) {
        if (problemTypes == null || problemTypes.isEmpty()) {
            return "无问题";
        }
        return problemTypes;
    }

    /**
     * 格式化问题类型显示
     */
    public static String formatProblemTypesDisplay(String problemTypes) {
        if (problemTypes == null || problemTypes.isEmpty()) {
            return "无";
        }
        return problemTypes;
    }

    /**
     * 计算平均质量评分
     */
    public static double calculateAverageQualityScore(Double score1, Double score2) {
        if (score1 == null && score2 == null) {
            return 0.0;
        }
        if (score1 == null) {
            return score2;
        }
        if (score2 == null) {
            return score1;
        }
        return (score1 + score2) / 2.0;
    }
}
