package com.ruoyi.system.domain.service;

import com.ruoyi.system.domain.entity.ProductCategory;
import java.util.List;
import java.util.ArrayList;

/**
 * 商品分类领域服务
 * 提供商品分类相关的业务逻辑和规则
 */
public class CategoryDomainService {

    /**
     * 验证分类名称
     */
    public static void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("分类名称长度不能超过100个字符");
        }
    }

    /**
     * 验证分类描述
     */
    public static void validateCategoryDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("分类描述长度不能超过500个字符");
        }
    }

    /**
     * 验证父分类ID
     */
    public static void validateParentId(Long parentId) {
        // 允许null（根分类）
        if (parentId != null && parentId <= 0) {
            throw new IllegalArgumentException("父分类ID必须大于0");
        }
    }

    /**
     * 生成分类名称
     */
    public static String generateCategoryName(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("前缀不能为空");
        }
        if (prefix.length() > 20) {
            throw new IllegalArgumentException("前缀长度不能超过20个字符");
        }
        return prefix + "-" + System.currentTimeMillis();
    }

    /**
     * 验证分类编码格式
     */
    public static boolean isValidCategoryCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return code.matches("^[A-Z0-9]{8}$");
    }

    /**
     * 验证排序权重
     */
    public static void validateSortOrder(Integer sortOrder) {
        if (sortOrder != null && sortOrder < 0) {
            throw new IllegalArgumentException("排序权重不能为负数");
        }
    }

    /**
     * 验证图标
     */
    public static void validateIcon(String icon) {
        if (icon != null && icon.length() > 50) {
            throw new IllegalArgumentException("图标长度不能超过50个字符");
        }
    }

    /**
     * 验证颜色
     */
    public static void validateColor(String color) {
        if (color != null && color.length() > 7) {
            throw new IllegalArgumentException("颜色值长度不能超过7个字符");
        }
    }

    /**
     * 验证优先级
     */
    public static void validatePriority(Integer priority) {
        if (priority != null && priority < 0) {
            throw new IllegalArgumentException("优先级不能为负数");
        }
    }

    /**
     * 生成分类层级路径
     */
    public static String generateCategoryPath(String categoryName, String parentPath) {
        if (parentPath == null || parentPath.isEmpty()) {
            return categoryName;
        }
        return parentPath + " / " + categoryName;
    }

    /**
     * 获取分类深度（假设层级不超过5层）
     */
    public static int calculateCategoryDepth(String categoryPath) {
        if (categoryPath == null || categoryPath.isEmpty()) {
            return 1;
        }
        return categoryPath.split(" / ").length;
    }

    /**
     * 验证分类是否可以归档
     */
    public static boolean canArchive(ProductCategory.CategoryStatus status, int childCount) {
        return status == ProductCategory.CategoryStatus.NORMAL && childCount == 0;
    }

    /**
     * 检查分类是否为根分类
     */
    public static boolean isRootCategory(Long parentId) {
        return parentId == null;
    }

    /**
     * 检查分类是否为叶子分类
     */
    public static boolean isLeafCategory(int childCount) {
        return childCount == 0;
    }
}
