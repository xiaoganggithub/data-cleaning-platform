package com.ruoyi.system.service.ai;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 清洗建议服务
 */
@Service
public class CleaningSuggestionService {

    private final ProblemDetectionService problemDetectionService;
    private final ImageQualityAnalysisService qualityAnalysisService;

    public CleaningSuggestionService(ProblemDetectionService problemDetectionService,
                                     ImageQualityAnalysisService qualityAnalysisService) {
        this.problemDetectionService = problemDetectionService;
        this.qualityAnalysisService = qualityAnalysisService;
    }

    /**
     * 获取清洗建议
     * @param imageUrl 图片URL
     * @return 清洗建议
     */
    public CleaningSuggestion getSuggestion(String imageUrl) {
        CleaningSuggestion suggestion = new CleaningSuggestion();
        suggestion.setImageUrl(imageUrl);

        // 获取AI分析结果
        ImageQualityAnalysisService.ImageQualityResult qualityResult =
            qualityAnalysisService.analyzeImageSync(imageUrl);
        suggestion.setQualityScore(qualityResult.getOverallScore());

        // 获取问题检测结果
        List<ProblemDetectionService.ProblemType> problems =
            problemDetectionService.detectProblems(imageUrl);
        suggestion.setProblems(problems);

        // 生成建议
        StringBuilder sb = new StringBuilder();

        // 基于质量问题评分给出建议
        if (qualityResult.getOverallScore() >= 90) {
            sb.append("【优秀】图片质量优秀，无需清洗处理。");
        } else if (qualityResult.getOverallScore() >= 80) {
            sb.append("【良好】图片质量良好，可选择性地进行轻微优化。");
        } else if (qualityResult.getOverallScore() >= 70) {
            sb.append("【一般】图片质量一般，建议进行清洗处理。");
        } else if (qualityResult.getOverallScore() >= 60) {
            sb.append("【较差】图片质量较差，建议进行清洗处理。");
        } else {
            sb.append("【差】图片质量差，建议重新获取图片。");
        }

        // 基于具体问题给出建议
        for (ProblemDetectionService.ProblemType problem : problems) {
            sb.append("\n\n【").append(problem.getName()).append("】");
            sb.append(getSuggestionForProblem(problem));
        }

        suggestion.setSuggestion(sb.toString());

        // 给出处理建议
        if (qualityResult.getOverallScore() >= 80 && problems.isEmpty()) {
            suggestion.setRecommendedAction("ADOPT"); // 直接采用
            suggestion.setReason("图片质量优秀，符合标准");
        } else if (qualityResult.getOverallScore() >= 70) {
            suggestion.setRecommendedAction("MANUAL_REVIEW"); // 人工审核
            suggestion.setReason("图片质量一般，需要人工审核确认");
        } else if (qualityResult.getOverallScore() >= 50) {
            suggestion.setRecommendedAction("CLEAN"); // 清洗后使用
            suggestion.setReason("图片存在问题，可尝试清洗后使用");
        } else {
            suggestion.setRecommendedAction("REJECT"); // 拒绝
            suggestion.setReason("图片质量太差，建议重新获取");
        }

        return suggestion;
    }

    /**
     * 根据问题类型获取具体建议
     */
    private String getSuggestionForProblem(ProblemDetectionService.ProblemType problem) {
        return switch (problem.getCode()) {
            case "BLUR" -> "图片模糊，可能影响商品识别。建议重新拍摄，确保相机稳定或使用更好的光源。";
            case "OCCLUSION" -> "图片存在遮挡，商品部分被遮挡。建议移除遮挡物后重新拍摄。";
            case "BACKGROUND_CLUTTER" -> "背景杂乱，可能影响商品主体识别。建议使用纯色或简单背景。";
            case "COLOR_DISTORTION" -> "色彩失真，可能影响商品真实颜色展示。建议校准相机或调整光线。";
            case "LOW_RESOLUTION" -> "分辨率过低，图片细节不清。建议使用更高分辨率的相机或更近距离拍摄。";
            case "LOW_LIGHTING" -> "光照不足，图片偏暗。建议增加光源或调整拍摄环境。";
            case "WRONG_ANGLE" -> "拍摄角度不正。建议调整商品或相机角度，确保商品正面朝前。";
            default -> "存在质量问题，建议检查并重新拍摄。";
        };
    }

    /**
     * 批量获取清洗建议
     */
    public List<CleaningSuggestion> batchGetSuggestions(List<String> imageUrls) {
        List<CleaningSuggestion> suggestions = new ArrayList<>();
        for (String url : imageUrls) {
            suggestions.add(getSuggestion(url));
        }
        return suggestions;
    }

    /**
     * 清洗建议
     */
    @Data
    public static class CleaningSuggestion {
        private String imageUrl;
        private double qualityScore;
        private List<ProblemDetectionService.ProblemType> problems;
        private String suggestion;
        private String recommendedAction; // ADOPT, CLEAN, MANUAL_REVIEW, REJECT
        private String reason;
    }
}
