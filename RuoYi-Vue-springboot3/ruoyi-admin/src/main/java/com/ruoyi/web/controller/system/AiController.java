package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ai.ImageQualityAnalysisService;
import com.ruoyi.system.service.ai.ProblemDetectionService;
import com.ruoyi.system.service.ai.CleaningSuggestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AI服务接口
 */
@RestController
@RequestMapping("/system/ai")
public class AiController {

    private final ImageQualityAnalysisService qualityAnalysisService;
    private final ProblemDetectionService problemDetectionService;
    private final CleaningSuggestionService suggestionService;

    public AiController(ImageQualityAnalysisService qualityAnalysisService,
                       ProblemDetectionService problemDetectionService,
                       CleaningSuggestionService suggestionService) {
        this.qualityAnalysisService = qualityAnalysisService;
        this.problemDetectionService = problemDetectionService;
        this.suggestionService = suggestionService;
    }

    /**
     * 分析图片质量
     */
    @PostMapping("/analyze/quality")
    public CompletableFuture<AjaxResult> analyzeImageQuality(@RequestBody AnalyzeRequest request) {
        return qualityAnalysisService.analyzeImage(request.getImageUrl())
                .thenApply(result -> AjaxResult.success(result));
    }

    /**
     * 检测图片问题
     */
    @PostMapping("/detect/problems")
    public AjaxResult detectProblems(@RequestBody AnalyzeRequest request) {
        List<ProblemDetectionService.ProblemType> problems =
            problemDetectionService.detectProblems(request.getImageUrl());
        return AjaxResult.success(problems);
    }

    /**
     * 获取清洗建议
     */
    @PostMapping("/suggest/cleaning")
    public AjaxResult getCleaningSuggestion(@RequestBody AnalyzeRequest request) {
        CleaningSuggestionService.CleaningSuggestion suggestion =
            suggestionService.getSuggestion(request.getImageUrl());
        return AjaxResult.success(suggestion);
    }

    /**
     * 批量获取清洗建议
     */
    @PostMapping("/suggest/cleaning/batch")
    public AjaxResult batchGetCleaningSuggestions(@RequestBody BatchAnalyzeRequest request) {
        List<CleaningSuggestionService.CleaningSuggestion> suggestions =
            suggestionService.batchGetSuggestions(request.getImageUrls());
        return AjaxResult.success(suggestions);
    }

    /**
     * 批量分析图片质量
     */
    @PostMapping("/analyze/quality/batch")
    public CompletableFuture<AjaxResult> batchAnalyzeImageQuality(@RequestBody BatchAnalyzeRequest request) {
        return qualityAnalysisService.batchAnalyzeImages(request.getImageUrls())
                .thenApply(result -> AjaxResult.success(result));
    }

    // ========== Request DTOs ==========

    public static class AnalyzeRequest {
        private String imageUrl;

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class BatchAnalyzeRequest {
        private List<String> imageUrls;

        public List<String> getImageUrls() { return imageUrls; }
        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    }
}
