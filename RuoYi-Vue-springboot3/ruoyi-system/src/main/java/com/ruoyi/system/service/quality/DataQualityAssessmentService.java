package com.ruoyi.system.service.quality;

import com.ruoyi.system.domain.entity.Dataset;
import com.ruoyi.system.domain.repository.DatasetRepository;
import com.ruoyi.system.domain.repository.ProductImageRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据质量评估服务
 * 评估数据集的5个维度：完整性、准确性、一致性、时效性、唯一性
 */
@Service
public class DataQualityAssessmentService {

    private final DatasetRepository datasetRepository;
    private final ProductImageRepository imageRepository;

    // 各维度权重
    private static final double COMPLETENESS_WEIGHT = 0.25;
    private static final double ACCURACY_WEIGHT = 0.30;
    private static final double CONSISTENCY_WEIGHT = 0.20;
    private static final double TIMELINESS_WEIGHT = 0.10;
    private static final double UNIQUENESS_WEIGHT = 0.15;

    public DataQualityAssessmentService(DatasetRepository datasetRepository,
                                      ProductImageRepository imageRepository) {
        this.datasetRepository = datasetRepository;
        this.imageRepository = imageRepository;
    }

    /**
     * 评估数据集质量
     */
    public QualityAssessmentResult assessDataset(Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));

        QualityAssessmentResult result = new QualityAssessmentResult();
        result.setDatasetId(datasetId);
        result.setDatasetName(dataset.getName());

        // 1. 评估完整性
        double completeness = assessCompleteness(dataset);
        result.setCompletenessScore(completeness);

        // 2. 评估准确性
        double accuracy = assessAccuracy(dataset);
        result.setAccuracyScore(accuracy);

        // 3. 评估一致性
        double consistency = assessConsistency(dataset);
        result.setConsistencyScore(consistency);

        // 4. 评估时效性
        double timeliness = assessTimeliness(dataset);
        result.setTimelinessScore(timeliness);

        // 5. 评估唯一性
        double uniqueness = assessUniqueness(dataset);
        result.setUniquenessScore(uniqueness);

        // 计算综合评分
        double overallScore = calculateOverallScore(result);
        result.setOverallScore(overallScore);

        // 确定质量等级
        result.setQualityGrade(determineQualityGrade(overallScore));

        return result;
    }

    /**
     * 评估完整性 - 数据是否完整
     * 维度：总图片数、已清洗数、缺失数据比例
     */
    private double assessCompleteness(Dataset dataset) {
        int totalCount = dataset.getTotalImageCount().getValue();
        int cleanedCount = dataset.getCleanedImageCount().getValue();

        if (totalCount == 0) {
            return 0.0;
        }

        // 已清洗率 (40%)
        double cleanedRate = (double) cleanedCount / totalCount * 100 * 0.4;

        // 数据完整率 - 假设预期数据都存在 (60%)
        double completenessRate = 100.0 * 0.6;

        return Math.min(cleanedRate + completenessRate, 100.0);
    }

    /**
     * 评估准确性 - 数据是否准确
     * 维度：AI评分、人工审核通过率
     */
    private double assessAccuracy(Dataset dataset) {
        // TODO: 实际实现需要查询AI评分和审核记录
        // 目前模拟一个准确率

        // 基于清洗状态的准确性评估
        int totalCount = dataset.getTotalImageCount().getValue();
        int cleanedCount = dataset.getCleanedImageCount().getValue();

        if (totalCount == 0) {
            return 0.0;
        }

        // 假设已清洗的数据都是准确的
        double baseAccuracy = (double) cleanedCount / totalCount * 100;

        // 考虑已审核的数据准确性更高
        double accuracy = baseAccuracy * 0.8 + 20; // 基准20分

        return Math.min(accuracy, 100.0);
    }

    /**
     * 评估一致性 - 数据是否一致
     * 维度：同类数据格式一致性、分类一致性
     */
    private double assessConsistency(Dataset dataset) {
        // TODO: 实际实现需要分析数据格式和分类
        // 目前模拟一个一致性评分

        // 假设数据集内部数据一致性较好
        return 85.0 + Math.random() * 10;
    }

    /**
     * 评估时效性 - 数据是否最新
     * 维度：数据更新时间、数据更新频率
     */
    private double assessTimeliness(Dataset dataset) {
        // TODO: 实际实现需要分析数据更新时间
        // 目前模拟一个时效性评分

        if (dataset.getUpdateTime() == null) {
            return 50.0;
        }

        long hoursSinceUpdate = (System.currentTimeMillis() - dataset.getUpdateTime().getTime()) / (1000 * 60 * 60);

        // 数据越新，时效性越好
        double timeliness = 100.0 - Math.min(hoursSinceUpdate * 0.5, 50);

        return Math.max(timeliness, 50.0);
    }

    /**
     * 评估唯一性 - 数据是否重复
     * 维度：MD5重复率、URL重复率
     */
    private double assessUniqueness(Dataset dataset) {
        // TODO: 实际实现需要查询重复数据
        // 目前模拟一个唯一性评分

        // 假设重复率较低
        double duplicationRate = Math.random() * 5; // 0-5%重复率
        return 100.0 - duplicationRate;
    }

    /**
     * 计算综合评分
     */
    private double calculateOverallScore(QualityAssessmentResult result) {
        return result.getCompletenessScore() * COMPLETENESS_WEIGHT
                + result.getAccuracyScore() * ACCURACY_WEIGHT
                + result.getConsistencyScore() * CONSISTENCY_WEIGHT
                + result.getTimelinessScore() * TIMELINESS_WEIGHT
                + result.getUniquenessScore() * UNIQUENESS_WEIGHT;
    }

    /**
     * 确定质量等级
     */
    private String determineQualityGrade(double overallScore) {
        if (overallScore >= 90) {
            return "A";
        } else if (overallScore >= 80) {
            return "B";
        } else if (overallScore >= 70) {
            return "C";
        } else if (overallScore >= 60) {
            return "D";
        } else {
            return "E";
        }
    }

    /**
     * 获取质量报告
     */
    public QualityReport generateReport(Long datasetId) {
        QualityAssessmentResult result = assessDataset(datasetId);

        QualityReport report = new QualityReport();
        report.setAssessmentResult(result);

        // 生成改进建议
        Map<String, String> suggestions = new HashMap<>();

        if (result.getCompletenessScore() < 80) {
            suggestions.put("completeness", "建议增加数据清洗进度，提高数据完整度");
        }
        if (result.getAccuracyScore() < 80) {
            suggestions.put("accuracy", "建议加强数据审核流程，提高数据准确性");
        }
        if (result.getConsistencyScore() < 80) {
            suggestions.put("consistency", "建议统一数据格式标准，提高数据一致性");
        }
        if (result.getTimelinessScore() < 80) {
            suggestions.put("timeliness", "建议定期更新数据，保持数据时效性");
        }
        if (result.getUniquenessScore() < 80) {
            suggestions.put("uniqueness", "建议进行数据去重处理，提高数据唯一性");
        }

        report.setSuggestions(suggestions);

        return report;
    }

    /**
     * 质量评估结果
     */
    @Data
    public static class QualityAssessmentResult {
        private Long datasetId;
        private String datasetName;
        private double overallScore;       // 综合评分
        private String qualityGrade;       // 质量等级 A/B/C/D/E
        private double completenessScore;  // 完整性评分
        private double accuracyScore;      // 准确性评分
        private double consistencyScore;   // 一致性评分
        private double timelinessScore;     // 时效性评分
        private double uniquenessScore;     // 唯一性评分
    }

    /**
     * 质量报告
     */
    @Data
    public static class QualityReport {
        private QualityAssessmentResult assessmentResult;
        private Map<String, String> suggestions;
    }
}
