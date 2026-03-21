package com.ruoyi.system.persistence.po;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据分片持久化对象
 */
@Data
public class DataShardPO implements Serializable {

    private Long shardId;
    private String shardCode;
    private Long datasetId;
    private String datasetCode;
    private String name;
    private String description;
    private Integer status;
    private Integer imageCount;
    private Integer processedCount;
    private Integer errorCount;
    private Double progressPercent;
    private Integer totalUrlCount;
    private Integer successImportCount;
    private Integer failedImportCount;
    private Date startTime;
    private Date completeTime;
    private Date createTime;
    private Date updateTime;
}
