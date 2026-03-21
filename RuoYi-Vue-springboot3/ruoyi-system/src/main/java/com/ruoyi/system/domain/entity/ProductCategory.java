package com.ruoyi.system.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * 商品图片分类领域实体 (充血模型)
 * 作为聚合根，管理ProductImage等实体
 */
@Data
public class ProductCategory implements Serializable {

    /**
     * 领域标识
     */
    private Long categoryId;

    /**
     * 分类编码（值对象）
     */
    private CategoryCode categoryCode;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 状态：正常(0)，已归档(1)
     */
    private CategoryStatus status;

    /**
     * 图像数量（值对象）
     */
    private ImageCount imageCount;

    /**
     * 子分类数量
     */
    private int childCount;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    // ========== 构造方法 ==========

    /**
     * 创建顶级分类
     */
    public ProductCategory(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("分类名称不能为空");
        }
        if (name.length() > 100) {
            throw new DomainException("分类名称长度不能超过100个字符");
        }

        this.categoryCode = new CategoryCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        this.name = name;
        this.description = description;
        this.parentId = null;
        this.status = CategoryStatus.NORMAL;
        this.imageCount = new ImageCount(0);
        this.childCount = 0;
        this.sortOrder = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 创建子分类
     */
    public ProductCategory(Long parentId, String name, String description) {
        if (parentId == null) {
            throw new DomainException("父分类ID不能为空");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("分类名称不能为空");
        }
        if (name.length() > 100) {
            throw new DomainException("分类名称长度不能超过100个字符");
        }

        this.categoryCode = new CategoryCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        this.parentId = parentId;
        this.name = name;
        this.description = description;
        this.status = CategoryStatus.NORMAL;
        this.imageCount = new ImageCount(0);
        this.childCount = 0;
        this.sortOrder = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 从持久化对象重建（用于反序列化）
     */
    public static ProductCategory rebuildFromPO(ProductCategoryPO po) {
        ProductCategory category = new ProductCategory();
        category.categoryId = po.getCategoryId();
        category.categoryCode = new CategoryCode(po.getCategoryCode());
        category.parentId = po.getParentId();
        category.name = po.getName();
        category.description = po.getDescription();
        category.status = CategoryStatus.valueOf(po.getStatus());
        category.imageCount = new ImageCount(po.getImageCount());
        category.childCount = po.getChildCount();
        category.sortOrder = po.getSortOrder();
        category.icon = po.getIcon();
        category.color = po.getColor();
        category.createTime = po.getCreateTime();
        category.updateTime = po.getUpdateTime();
        return category;
    }

    // ========== 领域行为 (充血模型) ==========

    /**
     * 归档分类
     */
    public void archive() {
        if (this.status != CategoryStatus.NORMAL) {
            throw new DomainException("分类状态不正确，只能从正常状态执行此操作");
        }
        if (this.childCount > 0) {
            throw new DomainException("该分类下存在子分类，无法归档");
        }
        this.status = CategoryStatus.ARCHIVED;
        this.updateTime = new Date();
        addDomainEvent(new CategoryArchivedEvent(this.categoryCode));
    }

    /**
     * 归档所有子分类
     */
    public void archiveChildren() {
        if (this.status != CategoryStatus.NORMAL) {
            throw new DomainException("分类状态不正确");
        }
        // 子分类的归档在子分类自身执行
        this.updateTime = new Date();
    }

    /**
     * 增加图像数量
     */
    public void addImageCount(int count) {
        this.imageCount = new ImageCount(this.imageCount.getValue() + count);
        this.updateTime = new Date();
    }

    /**
     * 减少图像数量
     */
    public void removeImageCount(int count) {
        int newValue = this.imageCount.getValue() - count;
        if (newValue < 0) {
            throw new DomainException("图像数量不能为负数");
        }
        this.imageCount = new ImageCount(newValue);
        this.updateTime = new Date();
    }

    /**
     * 增加子分类数量
     */
    public void addChildCount(int count) {
        this.childCount += count;
        this.updateTime = new Date();
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
     * 检查是否为根分类
     */
    public boolean isRoot() {
        return this.parentId == null;
    }

    /**
     * 检查是否为叶子分类（无子分类）
     */
    public boolean isLeaf() {
        return this.childCount == 0;
    }

    /**
     * 检查分类是否处于活动状态
     */
    public boolean isActive() {
        return this.status == CategoryStatus.NORMAL;
    }

    // ========== 领域服务 (领域逻辑) ==========

    /**
     * 生成分类名称
     */
    public static String generateCategoryName(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new DomainException("前缀不能为空");
        }
        if (prefix.length() > 20) {
            throw new DomainException("前缀长度不能超过20个字符");
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
        // 格式：XXXXXXXX（8位大写字母和数字）
        return code.matches("^[A-Z0-9]{8}$");
    }

    /**
     * 检查分类名称是否重复
     */
    public static boolean isNameUnique(String name, Long excludeCategoryId) {
        // 实现需要在Repository中查询
        return true;
    }

    /**
     * 计算分类层级深度
     */
    public int calculateDepth() {
        if (this.parentId == null) {
            return 1;
        }
        // 实现需要在Repository中查询父分类数量
        return 2;
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
     * 分类状态枚举
     */
    public enum CategoryStatus {
        NORMAL(0, "正常"),
        ARCHIVED(1, "已归档");

        private final int value;
        private final String description;

        CategoryStatus(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static CategoryStatus fromValue(int value) {
            for (CategoryStatus status : values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new DomainException("无效的分类状态值: " + value);
        }
    }

    /**
     * 分类编码值对象
     */
    @Data
    public static class CategoryCode {
        private final String value;

        public CategoryCode(String value) {
            if (value == null || value.isEmpty()) {
                throw new DomainException("分类编码不能为空");
            }
            if (value.length() != 8) {
                throw new DomainException("分类编码长度必须为8位");
            }
            this.value = value.toUpperCase();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CategoryCode that = (CategoryCode) o;
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
 * 分类已归档事件
 */
class CategoryArchivedEvent implements DomainEvent {
    private final ProductCategory.CategoryCode categoryCode;
    private final Date eventTime;

    public CategoryArchivedEvent(ProductCategory.CategoryCode categoryCode) {
        this.categoryCode = categoryCode;
        this.eventTime = new Date();
    }

    @Override
    public String getEventName() {
        return "CategoryArchived";
    }

    @Override
    public Date getEventTime() {
        return eventTime;
    }

    public ProductCategory.CategoryCode getCategoryCode() {
        return categoryCode;
    }
}
