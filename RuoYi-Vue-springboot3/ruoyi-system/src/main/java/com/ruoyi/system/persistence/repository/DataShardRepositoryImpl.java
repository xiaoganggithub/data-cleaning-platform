package com.ruoyi.system.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.entity.DataShard;
import com.ruoyi.system.domain.repository.DataShardRepository;
import com.ruoyi.system.persistence.mapper.DataShardMapper;
import com.ruoyi.system.persistence.po.DataShardPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据分片仓储实现
 */
@Repository
public class DataShardRepositoryImpl implements DataShardRepository {

    private final DataShardMapper shardMapper;

    public DataShardRepositoryImpl(DataShardMapper shardMapper) {
        this.shardMapper = shardMapper;
    }

    @Override
    public DataShard save(DataShard shard) {
        DataShardPO po = toPO(shard);
        if (shard.getShardId() == null) {
            shardMapper.insert(po);
        } else {
            shardMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<DataShard> findById(Long id) {
        DataShardPO po = shardMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<DataShard> findByDatasetId(Long datasetId) {
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataShardPO::getDatasetId, datasetId);
        wrapper.orderByAsc(DataShardPO::getCreateTime);
        return shardMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataShard> findByStatus(DataShard.ShardStatus status) {
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataShardPO::getStatus, status.getValue());
        return shardMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataShard> findByDatasetIdAndStatus(Long datasetId, DataShard.ShardStatus status) {
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataShardPO::getDatasetId, datasetId);
        wrapper.eq(DataShardPO::getStatus, status.getValue());
        return shardMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataShard> findAll() {
        return shardMapper.selectList(null).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataShard> findAllByDatasetId(Long datasetId) {
        return findByDatasetId(datasetId);
    }

    @Override
    public List<DataShard> findProcessingShards() {
        return findByStatus(DataShard.ShardStatus.PROCESSING);
    }

    @Override
    public List<DataShard> findCompletedShards() {
        return findByStatus(DataShard.ShardStatus.COMPLETED);
    }

    @Override
    public List<DataShard> findFailedShards() {
        return findByStatus(DataShard.ShardStatus.FAILED);
    }

    @Override
    public Optional<DataShard> findByName(String name) {
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataShardPO::getName, name);
        DataShardPO po = shardMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<DataShard> findByPage(int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<DataShardPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        shardMapper.selectPage(pageObj, null);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataShard> findByFilter(Long datasetId, DataShard.ShardStatus status, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<DataShardPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        if (datasetId != null) {
            wrapper.eq(DataShardPO::getDatasetId, datasetId);
        }
        if (status != null) {
            wrapper.eq(DataShardPO::getStatus, status.getValue());
        }
        shardMapper.selectPage(pageObj, wrapper);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return shardMapper.selectCount(null);
    }

    @Override
    public long countByDatasetId(Long datasetId) {
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataShardPO::getDatasetId, datasetId);
        return shardMapper.selectCount(wrapper);
    }

    @Override
    public Map<DataShard.ShardStatus, Long> countByStatus(Long datasetId) {
        List<DataShardPO> all;
        if (datasetId != null) {
            LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DataShardPO::getDatasetId, datasetId);
            all = shardMapper.selectList(wrapper);
        } else {
            all = shardMapper.selectList(null);
        }
        Map<DataShard.ShardStatus, Long> result = new HashMap<>();
        for (DataShard.ShardStatus status : DataShard.ShardStatus.values()) {
            result.put(status, 0L);
        }
        for (DataShardPO po : all) {
            DataShard.ShardStatus status = DataShard.ShardStatus.fromValue(po.getStatus());
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        return shardMapper.deleteById(id) > 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return shardMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<DataShardPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataShardPO::getShardCode, code);
        return shardMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Set<com.ruoyi.system.domain.event.DataShardEvent> getDomainEvents(Long id) {
        return Set.of();
    }

    @Override
    public void clearDomainEvents(Long id) {
    }

    // ========== Assembler Methods ==========

    private DataShard toEntity(DataShardPO po) {
        return DataShard.rebuildFromPO(po);
    }

    private DataShardPO toPO(DataShard entity) {
        DataShardPO po = new DataShardPO();
        po.setShardId(entity.getShardId());
        po.setShardCode(entity.getShardCode() != null ? entity.getShardCode().getValue() : null);
        po.setDatasetId(entity.getDatasetId());
        po.setDatasetCode(entity.getDatasetCode());
        po.setName(entity.getName());
        po.setDescription(entity.getDescription());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getValue() : null);
        po.setImageCount(entity.getImageCount());
        po.setProcessedCount(entity.getProcessedCount());
        po.setErrorCount(entity.getErrorCount());
        po.setProgressPercent(entity.getProgressPercent());
        po.setTotalUrlCount(entity.getTotalUrlCount());
        po.setSuccessImportCount(entity.getSuccessImportCount());
        po.setFailedImportCount(entity.getFailedImportCount());
        po.setStartTime(entity.getStartTime());
        po.setCompleteTime(entity.getCompleteTime());
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }
}
