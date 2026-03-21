package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.ProductImage;
import com.ruoyi.system.domain.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步导出服务
 */
@Service
public class AsyncExportService {

    @Autowired
    private ProductImageRepository imageRepository;

    // 存储导出任务状态
    private final Map<String, ExportTaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    /**
     * 异步导出图片数据到CSV
     * @param taskId 任务ID
     * @param datasetId 数据集ID
     * @param filePath 输出文件路径
     * @return 任务ID
     */
    @Async
    public CompletableFuture<String> exportImagesToCsvAsync(String taskId, Long datasetId, String filePath) {
        try {
            // 更新任务状态为进行中
            taskStatusMap.put(taskId, new ExportTaskStatus(taskId, "PROCESSING", 0, 0));

            // 查询所有图片
            List<ProductImage> images = imageRepository.findByDatasetId(datasetId);
            int total = images.size();
            int processed = 0;

            // 写入CSV文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // 写入表头
                writer.write("image_id,dataset_code,shopcode,vendorcode,sn,image_md5,image_url,image_status,quality_score,problem_types,clean_remark,create_time");
                writer.newLine();

                // 写入数据
                for (ProductImage image : images) {
                    StringBuilder line = new StringBuilder();
                    line.append(image.getImageId()).append(",");
                    line.append(image.getDatasetCode()).append(",");
                    line.append(image.getShopcode()).append(",");
                    line.append(image.getVendorcode() != null ? image.getVendorcode() : "").append(",");
                    line.append(image.getSn() != null ? image.getSn() : "").append(",");
                    line.append(image.getImageMd5()).append(",");
                    line.append(image.getImageUrl()).append(",");
                    line.append(image.getImageStatus() != null ? image.getImageStatus().getValue() : "").append(",");
                    line.append(image.getQualityScore() != null ? image.getQualityScore() : "").append(",");
                    line.append(image.getProblemTypes() != null ? image.getProblemTypes() : "").append(",");
                    line.append(image.getCleanRemark() != null ? image.getCleanRemark() : "").append(",");
                    line.append(image.getCreateTime() != null ? image.getCreateTime().toString() : "");

                    writer.write(line.toString());
                    writer.newLine();

                    processed++;
                    // 每100条更新一次进度
                    if (processed % 100 == 0) {
                        taskStatusMap.put(taskId, new ExportTaskStatus(taskId, "PROCESSING", processed, total));
                    }
                }
            }

            // 更新任务状态为完成
            taskStatusMap.put(taskId, new ExportTaskStatus(taskId, "COMPLETED", total, total));

            return CompletableFuture.completedFuture(taskId);

        } catch (Exception e) {
            // 更新任务状态为失败
            taskStatusMap.put(taskId, new ExportTaskStatus(taskId, "FAILED", 0, 0));
            return CompletableFuture.completedFuture(taskId);
        }
    }

    /**
     * 获取导出任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    public ExportTaskStatus getTaskStatus(String taskId) {
        return taskStatusMap.get(taskId);
    }

    /**
     * 取消导出任务
     * @param taskId 任务ID
     * @return 是否取消成功
     */
    public boolean cancelTask(String taskId) {
        ExportTaskStatus status = taskStatusMap.get(taskId);
        if (status != null && "PROCESSING".equals(status.status)) {
            taskStatusMap.put(taskId, new ExportTaskStatus(taskId, "CANCELLED", status.processed, status.total));
            return true;
        }
        return false;
    }

    /**
     * 创建导出任务
     * @param datasetId 数据集ID
     * @param filePath 文件路径
     * @return 任务ID
     */
    public String createExportTask(Long datasetId, String filePath) {
        String taskId = "export_" + System.currentTimeMillis();
        taskStatusMap.put(taskId, new ExportTaskStatus(taskId, "PENDING", 0, 0));

        // 启动异步导出
        exportImagesToCsvAsync(taskId, datasetId, filePath);

        return taskId;
    }

    /**
     * 导出任务状态
     */
    public static class ExportTaskStatus {
        public String taskId;
        public String status; // PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
        public int processed;
        public int total;

        public ExportTaskStatus(String taskId, String status, int processed, int total) {
            this.taskId = taskId;
            this.status = status;
            this.processed = processed;
            this.total = total;
        }

        public double getProgress() {
            return total > 0 ? (double) processed / total * 100 : 0;
        }
    }
}
