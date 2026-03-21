package com.ruoyi.system.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.persistence.po.DataShardPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据分片MyBatis-Plus Mapper
 */
@Mapper
public interface DataShardMapper extends BaseMapper<DataShardPO> {
}
