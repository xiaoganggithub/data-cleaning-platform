package com.ruoyi.system.persistence.po;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品图片持久化对象
 */
@Data
public class ProductImagePO implements Serializable {

    private Long imageId;
    private Long datasetId;
    private String datasetCode;
    private Long categoryId;
    private String categoryName;
    private String pluCode;
    private String pluName;
    private Long shardId;
    private String shopcode;
    private String vendorcode;
    private String sn;
    private String imageMd5;
    private String imageUrl;
    private Integer imageStatus;
    private Boolean locked;
    private Double qualityScore;
    private String cleanRemark;
    private String problemTypes;
    private Date createTime;
    private Date updateTime;
}
