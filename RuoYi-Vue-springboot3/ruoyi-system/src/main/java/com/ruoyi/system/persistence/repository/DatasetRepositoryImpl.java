package com.ruoyi.system.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.Dataset;
import com.ruoyi.system.domain.entity.DatasetStatus;
import com.ruoyi.system.domain.repository.DatasetRepository;
import com.ruoyi.system.persistence.mapper.DatasetMapper;
import com.ruoyi.system.persistence.po.DatasetPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据集仓储实现
 */
@Repository
public class DatasetRepositoryImpl implements DatasetRepository {

    private final DatasetMapper datasetMapper;

    public DatasetRepositoryImpl(DatasetMapper datasetMapper) {
        this.datasetMapper = datasetMapper;
    }

    @Override
    public Dataset save(Dataset dataset) {
        DatasetPO po = toPO(dataset);
        if (dataset.getDatasetId() == null) {
            datasetMapper.insert(po);
        } else {
            datasetMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<Dataset> findById(Long id) {
        DatasetPO po = datasetMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public Optional<Dataset> findByCode(String code) {
        LambdaQueryWrapper<DatasetPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetPO::getDatasetCode, code);
        DatasetPO po = datasetMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::toEntity);
    }

    @Override
    public List<Dataset> findByStatus(DatasetStatus status) {
        LambdaQueryWrapper<DatasetPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetPO::getStatus, status.getValue());
        return datasetMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findByStatuses(List<DatasetStatus> statuses) {
        List<Integer> statusValues = statuses.stream()
                .map(DatasetStatus::getValue)
                .collect(Collectors.toList());
        LambdaQueryWrapper<DatasetPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DatasetPO::getStatus, statusValues);
        return datasetMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findAll() {
        return datasetMapper.selectList(null).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findByPage(int page, int size) {
        // Using MyBatis-Plus pagination
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<DatasetPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        datasetMapper.selectPage(pageObj, null);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> searchByPage(String datasetCode, String name, String beginTime, String endTime, int page, int size) {
        LambdaQueryWrapper<DatasetPO> wrapper = buildSearchWrapper(datasetCode, name, beginTime, endTime);
        wrapper.orderByDesc(DatasetPO::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<DatasetPO> pageObj =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        datasetMapper.selectPage(pageObj, wrapper);
        return pageObj.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countSearch(String datasetCode, String name, String beginTime, String endTime) {
        LambdaQueryWrapper<DatasetPO> wrapper = buildSearchWrapper(datasetCode, name, beginTime, endTime);
        return datasetMapper.selectCount(wrapper);
    }

    private LambdaQueryWrapper<DatasetPO> buildSearchWrapper(String datasetCode, String name, String beginTime, String endTime) {
        LambdaQueryWrapper<DatasetPO> wrapper = new LambdaQueryWrapper<>();

        if (datasetCode != null && !datasetCode.isBlank()) {
            wrapper.like(DatasetPO::getDatasetCode, datasetCode);
        }
        if (name != null && !name.isBlank()) {
            wrapper.like(DatasetPO::getName, name);
        }
        if (beginTime != null && !beginTime.isBlank()) {
            wrapper.ge(DatasetPO::getCreateTime, beginTime + " 00:00:00");
        }
        if (endTime != null && !endTime.isBlank()) {
            wrapper.le(DatasetPO::getCreateTime, endTime + " 23:59:59");
        }

        return wrapper;
    }

    @Override
    public long count() {
        return datasetMapper.selectCount(null);
    }

    @Override
    public Map<DatasetStatus, Long> countByStatus() {
        List<DatasetPO> all = datasetMapper.selectList(null);
        Map<DatasetStatus, Long> result = new HashMap<>();
        for (DatasetStatus status : DatasetStatus.values()) {
            result.put(status, 0L);
        }
        for (DatasetPO po : all) {
            DatasetStatus status = DatasetStatus.fromValue(po.getStatus());
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        return datasetMapper.deleteById(id) > 0;
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<DatasetPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetPO::getDatasetCode, code);
        return datasetMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Set<com.ruoyi.system.domain.event.DatasetEvent> getDomainEvents(Long id) {
        // Domain events are stored in memory, return empty set
        return Set.of();
    }

    @Override
    public void clearDomainEvents(Long id) {
        // Domain events are stored in memory, nothing to clear
    }

    // ========== Assembler Methods ==========

    private Dataset toEntity(DatasetPO po) {
        return Dataset.rebuildFromPO(po);
    }

    private DatasetPO toPO(Dataset entity) {
        DatasetPO po = new DatasetPO();
        po.setDatasetId(entity.getDatasetId());
        po.setDatasetCode(entity.getDatasetCode() != null ? entity.getDatasetCode().getValue() : null);
        po.setName(entity.getName());
        po.setDescription(entity.getDescription());
        po.setStatus(entity.getStatus() != null ? entity.getStatus().getValue() : null);
        po.setTotalImageCount(entity.getTotalImageCount() != null ? entity.getTotalImageCount().getValue() : null);
        po.setCleanedImageCount(entity.getCleanedImageCount() != null ? entity.getCleanedImageCount().getValue() : null);
        po.setCreateTime(entity.getCreateTime());
        po.setUpdateTime(entity.getUpdateTime());
        return po;
    }
}
