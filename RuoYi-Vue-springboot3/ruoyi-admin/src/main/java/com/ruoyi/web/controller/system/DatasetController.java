package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.application.service.DatasetApplicationService;
import com.ruoyi.system.domain.entity.Dataset;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据集管理接口
 */
@RestController
@RequestMapping("/system/dataset")
public class DatasetController {

    private final DatasetApplicationService datasetService;

    public DatasetController(DatasetApplicationService datasetService) {
        this.datasetService = datasetService;
    }

    /**
     * 创建数据集
     */
    @PostMapping("/create")
    public AjaxResult createDataset(@RequestBody CreateDatasetRequest request) {
        Dataset dataset = datasetService.createDataset(request.getName(), request.getDescription());
        return AjaxResult.success(dataset);
    }

    /**
     * 初始化数据集
     */
    @PostMapping("/{datasetId}/initialize")
    public AjaxResult initializeDataset(@PathVariable Long datasetId) {
        Dataset dataset = datasetService.initializeDataset(datasetId);
        return AjaxResult.success(dataset);
    }

    /**
     * 开始清洗
     */
    @PostMapping("/{datasetId}/start-cleaning")
    public AjaxResult startCleaning(@PathVariable Long datasetId) {
        Dataset dataset = datasetService.startCleaning(datasetId);
        return AjaxResult.success(dataset);
    }

    /**
     * 完成清洗
     */
    @PostMapping("/{datasetId}/complete-cleaning")
    public AjaxResult completeCleaning(@PathVariable Long datasetId, @RequestBody CompleteCleaningRequest request) {
        Dataset dataset = datasetService.completeCleaning(datasetId, request.getCleanedImageCount());
        return AjaxResult.success(dataset);
    }

    /**
     * 审核通过
     */
    @PostMapping("/{datasetId}/approve")
    public AjaxResult approve(@PathVariable Long datasetId) {
        Dataset dataset = datasetService.approve(datasetId);
        return AjaxResult.success(dataset);
    }

    /**
     * 发布数据集
     */
    @PostMapping("/{datasetId}/publish")
    public AjaxResult publish(@PathVariable Long datasetId) {
        Dataset dataset = datasetService.publish(datasetId);
        return AjaxResult.success(dataset);
    }

    /**
     * 归档数据集
     */
    @PostMapping("/{datasetId}/archive")
    public AjaxResult archive(@PathVariable Long datasetId) {
        Dataset dataset = datasetService.archive(datasetId);
        return AjaxResult.success(dataset);
    }

    /**
     * 删除数据集
     */
    @DeleteMapping("/{datasetId}")
    public AjaxResult deleteDataset(@PathVariable Long datasetId) {
        datasetService.deleteDataset(datasetId);
        return AjaxResult.success();
    }

    /**
     * 查询数据集
     */
    @GetMapping("/{datasetId}")
    public AjaxResult getDataset(@PathVariable Long datasetId) {
        return datasetService.findById(datasetId)
                .map(AjaxResult::success)
                .orElse(AjaxResult.error("数据集不存在"));
    }

    /**
     * 根据编码查询
     */
    @GetMapping("/code/{datasetCode}")
    public AjaxResult getDatasetByCode(@PathVariable String datasetCode) {
        return datasetService.findByCode(datasetCode)
                .map(AjaxResult::success)
                .orElse(AjaxResult.error("数据集不存在"));
    }

    /**
     * 查询所有数据集
     */
    @GetMapping("/list")
    public AjaxResult listDatasets() {
        List<Dataset> datasets = datasetService.findAll();
        return AjaxResult.success(datasets);
    }

    /**
     * 根据状态查询
     */
    @GetMapping("/list/{status}")
    public AjaxResult listByStatus(@PathVariable Integer status) {
        Dataset.DatasetStatus datasetStatus = Dataset.DatasetStatus.fromValue(status);
        List<Dataset> datasets = datasetService.findByStatus(datasetStatus);
        return AjaxResult.success(datasets);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public AjaxResult pageDatasets(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        List<Dataset> datasets = datasetService.findByPage(page, size);
        return AjaxResult.success(datasets);
    }

    /**
     * 统计数据集数量
     */
    @GetMapping("/count")
    public AjaxResult countDatasets() {
        long count = datasetService.count();
        return AjaxResult.success(count);
    }

    /**
     * 各状态统计
     */
    @GetMapping("/statistics/status")
    public AjaxResult statisticsByStatus() {
        Map<Dataset.DatasetStatus, Long> statistics = datasetService.countByStatus();
        return AjaxResult.success(statistics);
    }

    // ========== Request DTOs ==========

    public static class CreateDatasetRequest {
        private String name;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class CompleteCleaningRequest {
        private int cleanedImageCount;

        public int getCleanedImageCount() { return cleanedImageCount; }
        public void setCleanedImageCount(int cleanedImageCount) { this.cleanedImageCount = cleanedImageCount; }
    }
}
