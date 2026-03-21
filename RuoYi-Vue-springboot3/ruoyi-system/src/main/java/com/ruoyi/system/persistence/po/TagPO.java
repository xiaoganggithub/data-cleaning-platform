package com.ruoyi.system.persistence.po;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 标签持久化对象
 */
@Data
public class TagPO implements Serializable {

    private Long tagId;
    private String tagCode;
    private String name;
    private Integer type;
    private Long parentId;
    private String description;
    private Integer status;
    private Integer usageCount;
    private Integer imageCount;
    private Integer sortOrder;
    private String icon;
    private String color;
    private Integer priority;
    private Date createTime;
    private Date updateTime;
}
