package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.quality.DataQualityAssessmentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据质量评估接口
 */
@RestController
@RequestMapping("/system/quality")
public class QualityController {

    private final DataQualityAssessmentService qualityService;

    public QualityController(DataQualityAssessmentService qualityService) {
        this.qualityService = qualityService;
    }

    /**
     * 评估数据集质量
     */
    @GetMapping("/assess/{datasetId}")
    public AjaxResult assessDataset(@PathVariable Long datasetId) {
        DataQualityAssessmentService.QualityAssessmentResult result =
            qualityService.assessDataset(datasetId);
        return AjaxResult.success(result);
    }

    /**
     * 生成质量报告
     */
    @GetMapping("/report/{datasetId}")
    public AjaxResult generateReport(@PathVariable Long datasetId) {
        DataQualityAssessmentService.QualityReport report =
            qualityService.generateReport(datasetId);
        return AjaxResult.success(report);
    }
}
