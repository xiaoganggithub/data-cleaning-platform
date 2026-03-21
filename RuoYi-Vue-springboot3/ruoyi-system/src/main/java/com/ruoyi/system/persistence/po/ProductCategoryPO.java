package com.ruoyi.system.persistence.po;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品分类持久化对象
 */
@Data
public class ProductCategoryPO implements Serializable {

    private Long categoryId;
    private String categoryCode;
    private Long parentId;
    private String name;
    private String description;
    private Integer status;
    private Integer imageCount;
    private Integer childCount;
    private Integer sortOrder;
    private String icon;
    private String color;
    private Date createTime;
    private Date updateTime;
}
