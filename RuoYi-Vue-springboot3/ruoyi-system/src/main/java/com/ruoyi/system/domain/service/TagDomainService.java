package com.ruoyi.system.domain.service;

import com.ruoyi.system.domain.entity.Tag;
import com.ruoyi.system.domain.entity.Tag.TagType;

/**
 * 标签领域服务
 * 提供标签相关的业务逻辑和规则
 */
public class TagDomainService {

    /**
     * 验证标签名称
     */
    public static void validateTagName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("标签名称长度不能超过50个字符");
        }
    }

    /**
     * 验证标签描述
     */
    public static void validateTagDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("标签描述长度不能超过200个字符");
        }
    }

    /**
     * 验证父标签ID
     */
    public static void validateParentId(Long parentId) {
        // 允许null（根标签）
        if (parentId != null && parentId <= 0) {
            throw new IllegalArgumentException("父标签ID必须大于0");
        }
    }

    /**
     * 验证标签类型
     */
    public static void validateTagType(TagType type) {
        if (type == null) {
            throw new IllegalArgumentException("标签类型不能为空");
        }
    }

    /**
     * 生成标签名称
     */
    public static String generateTagName(TagType type, String description) {
        if (type == null) {
            throw new IllegalArgumentException("标签类型不能为空");
        }
        String typeStr = type.getDescription();
        String desc = description != null ? description.substring(0, Math.min(10, description.length())) : "tag";
        return typeStr + "-" + desc;
    }

    /**
     * 验证标签编码格式
     */
    public static boolean isValidTagCode(String code) {
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
     * 验证标签类型枚举值
     */
    public static boolean isValidTagType(int typeValue) {
        try {
            TagType.valueOf(typeValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 根据类型获取标签名称前缀
     */
    public static String getTagNamePrefix(TagType type) {
        if (type == null) {
            return "其他";
        }
        return type.getDescription();
    }

    /**
     * 获取标签层级显示路径
     */
    public static String getTagPathDisplay(String tagName, String parentPath) {
        if (parentPath == null || parentPath.isEmpty()) {
            return tagName;
        }
        return parentPath + " > " + tagName;
    }

    /**
     * 验证标签是否可以归档
     */
    public static boolean canArchive(Tag.TagStatus status) {
        return status == Tag.TagStatus.NORMAL;
    }

    /**
     * 检查标签是否为根标签
     */
    public static boolean isRootTag(Long parentId) {
        return parentId == null;
    }
}
