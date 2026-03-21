package com.ruoyi.system.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 标签领域实体 (充血模型)
 * 支持多级标签层级结构
 */
@Data
public class Tag implements Serializable {

    /**
     * 领域标识
     */
    private Long tagId;

    /**
     * 标签编码（值对象）
     */
    private TagCode tagCode;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签类型：质量类(0)，清晰度类(1)，模糊类(2)，遮挡类(3)，背景类(4)，其他(9)
     */
    private TagType type;

    /**
     * 父标签ID
     */
    private Long parentId;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 状态：正常(0)，已归档(1)
     */
    private TagStatus status;

    /**
     * 使用次数
     */
    private int usageCount;

    /**
     * 图像数量
     */
    private int imageCount;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 图标
     */
    private String icon;

    /**
     * 颜色
     */
    private String color;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    // ========== 构造方法 ==========

    /**
     * 创建根标签
     */
    public Tag(String name, TagType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("标签名称不能为空");
        }
        if (name.length() > 50) {
            throw new DomainException("标签名称长度不能超过50个字符");
        }
        if (type == null) {
            throw new DomainException("标签类型不能为空");
        }

        this.tagCode = new TagCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        this.name = name;
        this.type = type;
        this.parentId = null;
        this.status = TagStatus.NORMAL;
        this.usageCount = 0;
        this.imageCount = 0;
        this.sortOrder = 0;
        this.priority = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 创建子标签
     */
    public Tag(Long parentId, String name, TagType type) {
        if (parentId == null) {
            throw new DomainException("父标签ID不能为空");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("标签名称不能为空");
        }
        if (name.length() > 50) {
            throw new DomainException("标签名称长度不能超过50个字符");
        }
        if (type == null) {
            throw new DomainException("标签类型不能为空");
        }

        this.tagCode = new TagCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.status = TagStatus.NORMAL;
        this.usageCount = 0;
        this.imageCount = 0;
        this.sortOrder = 0;
        this.priority = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 从持久化对象重建（用于反序列化）
     */
    public static Tag rebuildFromPO(TagPO po) {
        Tag tag = new Tag();
        tag.tagId = po.getTagId();
        tag.tagCode = new TagCode(po.getTagCode());
        tag.name = po.getName();
        tag.type = TagType.valueOf(po.getType());
        tag.parentId = po.getParentId();
        tag.description = po.getDescription();
        tag.status = TagStatus.valueOf(po.getStatus());
        tag.usageCount = po.getUsageCount();
        tag.imageCount = po.getImageCount();
        tag.sortOrder = po.getSortOrder();
        tag.icon = po.getIcon();
        tag.color = po.getColor();
        tag.priority = po.getPriority();
        tag.createTime = po.getCreateTime();
        tag.updateTime = po.getUpdateTime();
        return tag;
    }

    // ========== 领域行为 (充血模型) ==========

    /**
     * 归档标签
     */
    public void archive() {
        if (this.status != TagStatus.NORMAL) {
            throw new DomainException("标签状态不正确，只能从正常状态执行此操作");
        }
        this.status = TagStatus.ARCHIVED;
        this.updateTime = new Date();
        addDomainEvent(new TagArchivedEvent(this.tagCode));
    }

    /**
     * 增加使用次数
     */
    public void incrementUsageCount() {
        this.usageCount++;
        this.updateTime = new Date();
    }

    /**
     * 增加图像数量
     */
    public void incrementImageCount() {
        this.imageCount++;
        this.updateTime = new Date();
    }

    /**
     * 减少使用次数
     */
    public void decrementUsageCount() {
        if (this.usageCount > 0) {
            this.usageCount--;
            this.updateTime = new Date();
        }
    }

    /**
     * 减少图像数量
     */
    public void decrementImageCount() {
        if (this.imageCount > 0) {
            this.imageCount--;
            this.updateTime = new Date();
        }
    }

    /**
     * 更新排序权重
     */
    public void updateSortOrder(Integer sortOrder) {
        if (sortOrder == null || sortOrder < 0) {
            throw new DomainException("排序权重不能为负数");
        }
        this.sortOrder = sortOrder;
        this.updateTime = new Date();
    }

    /**
     * 更新优先级
     */
    public void updatePriority(Integer priority) {
        if (priority == null || priority < 0) {
            throw new DomainException("优先级不能为负数");
        }
        this.priority = priority;
        this.updateTime = new Date();
    }

    /**
     * 检查是否为根标签
     */
    public boolean isRoot() {
        return this.parentId == null;
    }

    /**
     * 检查是否为叶子标签（无子标签）
     */
    public boolean isLeaf() {
        // 子标签数量需要在Repository中查询
        return true;
    }

    /**
     * 检查标签是否处于活动状态
     */
    public boolean isActive() {
        return this.status == TagStatus.NORMAL;
    }

    // ========== 领域服务 (领域逻辑) ==========

    /**
     * 生成标签名称
     */
    public static String generateTagName(String type, String description) {
        if (type == null || type.trim().isEmpty()) {
            throw new DomainException("标签类型不能为空");
        }
        return type + "-" + (description != null ? description.substring(0, Math.min(10, description.length())) : "tag");
    }

    /**
     * 验证标签编码格式
     */
    public static boolean isValidTagCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        // 格式：XXXXXXXX（8位大写字母和数字）
        return code.matches("^[A-Z0-9]{8}$");
    }

    /**
     * 检查标签名称是否重复
     */
    public static boolean isNameUnique(String name, Long excludeTagId) {
        // 实现需要在Repository中查询
        return true;
    }

    // ========== 领域事件 ==========

    private Set<DomainEvent> domainEvents = new HashSet<>();

    public void addDomainEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    public Set<DomainEvent> pullDomainEvents() {
        Set<DomainEvent> events = new HashSet<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // ========== 枚举类 ==========

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

    /**
     * 标签状态枚举
     */
    public enum TagStatus {
        NORMAL(0, "正常"),
        ARCHIVED(1, "已归档");

        private final int value;
        private final String description;

        TagStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static TagStatus fromValue(int value) {
            for (TagStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new DomainException("无效的标签状态值: " + value);
        }
    }

    /**
     * 标签编码值对象
     */
    @Data
    public static class TagCode {
        private final String value;

        public TagCode(String value) {
            if (value == null || value.isEmpty()) {
                throw new DomainException("标签编码不能为空");
            }
            if (value.length() != 8) {
                throw new DomainException("标签编码长度必须为8位");
            }
            this.value = value.toUpperCase();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TagCode that = (TagCode) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}

/**
 * 领域异常
 */
class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}

/**
 * 领域事件接口
 */
interface DomainEvent extends Serializable {
    String getEventName();
    Date getEventTime();
}

/**
 * 标签已归档事件
 */
class TagArchivedEvent implements DomainEvent {
    private final Tag.TagCode tagCode;
    private final Date eventTime;

    public TagArchivedEvent(Tag.TagCode tagCode) {
        this.tagCode = tagCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "TagArchived";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public Tag.TagCode getTagCode() {
        return tagCode;
    }
}
