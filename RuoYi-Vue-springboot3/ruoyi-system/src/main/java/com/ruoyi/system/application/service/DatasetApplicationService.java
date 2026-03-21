package com.ruoyi.system.application.service;

import com.ruoyi.system.domain.entity.Dataset;
import com.ruoyi.system.domain.entity.DatasetStatus;
import com.ruoyi.system.domain.repository.DatasetRepository;
import com.ruoyi.system.domain.service.DatasetDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * 数据集应用服务
 * 负责数据集业务用例的编排和事务管理
 */
@Service
public class DatasetApplicationService {

    private final DatasetRepository datasetRepository;

    public DatasetApplicationService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    /**
     * 创建数据集
     */
    @Transactional
    public Dataset createDataset(String name, String description) {
        // Validate
        DatasetDomainService.validateDatasetName(name);
        DatasetDomainService.validateDatasetDescription(description);

        // Create domain entity - Dataset constructor takes name and description
        Dataset dataset = new Dataset(name, description);

        // Save and return
        return datasetRepository.save(dataset);
    }

    /**
     * 初始化数据集
     */
    @Transactional
    public Dataset initializeDataset(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.initialize();

        return datasetRepository.save(dataset);
    }

    /**
     * 开始清洗数据集
     */
    @Transactional
    public Dataset startCleaning(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.startCleaning();

        return datasetRepository.save(dataset);
    }

    /**
     * 完成清洗
     */
    @Transactional
    public Dataset completeCleaning(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.completeCleaning();

        return datasetRepository.save(dataset);
    }

    /**
     * 审核通过
     */
    @Transactional
    public Dataset approve(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.approve();

        return datasetRepository.save(dataset);
    }

    /**
     * 发布数据集
     */
    @Transactional
    public Dataset publish(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.publish();

        return datasetRepository.save(dataset);
    }

    /**
     * 归档数据集
     */
    @Transactional
    public Dataset archive(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.archive();

        return datasetRepository.save(dataset);
    }

    /**
     * 删除数据集
     */
    @Transactional
    public void deleteDataset(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.delete();
        datasetRepository.save(dataset);
    }

    /**
     * 更新统计信息
     */
    @Transactional
    public Dataset updateStatistics(Long datasetId, int totalImageCount, int cleanedImageCount) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        dataset.setTotalImageCount(new com.ruoyi.system.domain.valueobject.ImageCount(totalImageCount));
        dataset.setCleanedImageCount(new com.ruoyi.system.domain.valueobject.ImageCount(cleanedImageCount));
        dataset.updateStatistics();

        return datasetRepository.save(dataset);
    }

    /**
     * 查询数据集
     */
    public Optional<Dataset> findById(Long datasetId) {
        return datasetRepository.findById(datasetId);
    }

    /**
     * 根据编码查询数据集
     */
    public Optional<Dataset> findByCode(String datasetCode) {
        return datasetRepository.findByCode(datasetCode);
    }

    /**
     * 查询所有数据集
     */
    public List<Dataset> findAll() {
        return datasetRepository.findAll();
    }

    /**
     * 根据状态查询数据集
     */
    public List<Dataset> findByStatus(DatasetStatus status) {
        return datasetRepository.findByStatus(status);
    }

    /**
     * 分页查询数据集
     */
    public List<Dataset> findByPage(int page, int size) {
        return datasetRepository.findByPage(page, size);
    }

    /**
     * 搜索并分页查询数据集
     */
    public List<Dataset> searchByPage(String datasetCode, String name, String beginTime, String endTime, int page, int size) {
        return datasetRepository.searchByPage(datasetCode, name, beginTime, endTime, page, size);
    }

    /**
     * 统计搜索结果数量
     */
    public long countSearch(String datasetCode, String name, String beginTime, String endTime) {
        return datasetRepository.countSearch(datasetCode, name, beginTime, endTime);
    }

    /**
     * 统计数据集数量
     */
    public long count() {
        return datasetRepository.count();
    }

    /**
     * 各状态统计
     */
    public Map<DatasetStatus, Long> countByStatus() {
        return datasetRepository.countByStatus();
    }

    /**
     * 生成数据集编码
     */
    private String generateDatasetCode() {
        // 使用时间戳+随机数生成6位编码
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int) (Math.random() * 100));
        String code = (timestamp + random).substring(0, 6).toUpperCase();

        // 确保编码唯一
        while (datasetRepository.existsByCode(code)) {
            code = String.valueOf((int) (Math.random() * 1000000)).toUpperCase();
            while (code.length() < 6) {
                code = "0" + code;
            }
        }

        return code;
    }
}
