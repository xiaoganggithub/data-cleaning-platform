package com.ruoyi.system.persistence.po;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据集持久化对象
 */
@Data
public class DatasetPO implements Serializable {

    private Long datasetId;
    private String datasetCode;
    private String name;
    private String description;
    private Integer status;
    private Integer totalImageCount;
    private Integer cleanedImageCount;
    private Date createTime;
    private Date updateTime;
}
