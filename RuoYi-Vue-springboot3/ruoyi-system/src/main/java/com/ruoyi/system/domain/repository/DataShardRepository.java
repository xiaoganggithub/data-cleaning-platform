package com.ruoyi.system.domain.repository;

import com.ruoyi.system.domain.entity.DataShard;
import java.util.Optional;
import java.util.List;

/**
 * 数据分片领域仓储接口
 * 定义数据分片相关的数据访问操作
 */
public interface DataShardRepository {

    /**
     * 保存数据分片
     * @param shard 数据分片实体
     * @return 保存后的数据分片
     */
    DataShard save(DataShard shard);

    /**
     * 根据ID查询数据分片
     * @param id 数据分片ID
     * @return 数据分片实体
     */
    Optional<DataShard> findById(Long id);

    /**
     * 根据数据集ID查询分片列表
     * @param datasetId 数据集ID
     * @return 数据分片列表
     */
    List<DataShard> findByDatasetId(Long datasetId);

    /**
     * 根据状态查询数据分片
     * @param status 状态
     * @return 数据分片列表
     */
    List<DataShard> findByStatus(DataShard.ShardStatus status);

    /**
     * 根据数据集ID和状态查询数据分片
     * @param datasetId 数据集ID
     * @param status 状态
     * @return 数据分片列表
     */
    List<DataShard> findByDatasetIdAndStatus(Long datasetId, DataShard.ShardStatus status);

    /**
     * 查询所有数据分片
     * @return 数据分片列表
     */
    List<DataShard> findAll();

    /**
     * 根据数据集ID查询所有数据分片
     * @param datasetId 数据集ID
     * @return 数据分片列表
     */
    List<DataShard> findAllByDatasetId(Long datasetId);

    /**
     * 查询正在处理的数据分片
     * @return 数据分片列表
     */
    List<DataShard> findProcessingShards();

    /**
     * 查询已完成的数据分片
     * @return 数据分片列表
     */
    List<DataShard> findCompletedShards();

    /**
     * 查询失败的数据分片
     * @return 数据分片列表
     */
    List<DataShard> findFailedShards();

    /**
     * 根据名称查询
     * @param name 分片名称
     * @return 数据分片实体
     */
    Optional<DataShard> findByName(String name);

    /**
     * 查询所有数据分片
     * @return 数据分片列表
     */
    List<DataShard> findAll();

    /**
     * 分页查询数据分片
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 数据分片分页结果
     */
    List<DataShard> findByPage(int page, int size);

    /**
     * 分页查询数据分片（带过滤条件）
     * @param datasetId 数据集ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 数据分片列表
     */
    List<DataShard> findByFilter(Long datasetId, DataShard.ShardStatus status, int page, int size);

    /**
     * 统计数据分片数量
     * @return 数据分片总数
     */
    long count();

    /**
     * 根据数据集ID统计数据分片数量
     * @param datasetId 数据集ID
     * @return 数据分片总数
     */
    long countByDatasetId(Long datasetId);

    /**
     * 统计各状态数据分片数量
     * @param datasetId 数据集ID
     * @return 状态统计
     */
    java.util.Map<DataShard.ShardStatus, Long> countByStatus(Long datasetId);

    /**
     * 删除数据分片
     * @param id 数据分片ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除数据分片
     * @param ids 数据分片ID列表
     * @return 删除数量
     */
    int deleteByIds(List<Long> ids);

    /**
     * 检查编码是否存在
     * @param code 分片编码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 获取领域事件
     * @param id 数据分片ID
     * @return 领域事件集合
     */
    Set<com.ruoyi.system.domain.event.DataShardEvent> getDomainEvents(Long id);

    /**
     * 清除领域事件
     * @param id 数据分片ID
     */
    void clearDomainEvents(Long id);
}
