package com.ruoyi.system.service.ai;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 问题检测服务
 */
@Service
public class ProblemDetectionService {

    /**
     * 检测图片问题类型
     * @param imageUrl 图片URL
     * @return 问题类型列表
     */
    public List<ProblemType> detectProblems(String imageUrl) {
        List<ProblemType> problems = new ArrayList<>();

        // TODO: 调用实际的AI服务进行问题检测
        // 目前返回模拟数据

        // 模拟问题检测
        double random = Math.random();

        if (random < 0.2) {
            problems.add(new ProblemType("模糊", "BLUR", 0.8));
        }
        if (random < 0.15) {
            problems.add(new ProblemType("遮挡", "OCCLUSION", 0.7));
        }
        if (random < 0.25) {
            problems.add(new ProblemType("背景杂乱", "BACKGROUND_CLUTTER", 0.6));
        }
        if (random < 0.1) {
            problems.add(new ProblemType("色彩失真", "COLOR_DISTORTION", 0.5));
        }
        if (random < 0.15) {
            problems.add(new ProblemType("分辨率过低", "LOW_RESOLUTION", 0.9));
        }
        if (random < 0.1) {
            problems.add(new ProblemType("光照不足", "LOW_LIGHTING", 0.6));
        }
        if (random < 0.1) {
            problems.add(new ProblemType("角度不正", "WRONG_ANGLE", 0.5));
        }

        return problems;
    }

    /**
     * 获取问题严重程度
     */
    public String getProblemSeverity(ProblemType problem) {
        if (problem.getConfidence() >= 0.8) {
            return "严重";
        } else if (problem.getConfidence() >= 0.6) {
            return "中等";
        } else {
            return "轻微";
        }
    }

    /**
     * 批量检测问题
     */
    public Map<String, List<ProblemType>> batchDetectProblems(List<String> imageUrls) {
        Map<String, List<ProblemType>> results = new HashMap<>();
        for (String url : imageUrls) {
            results.put(url, detectProblems(url));
        }
        return results;
    }

    /**
     * 问题类型
     */
    @Data
    public static class ProblemType {
        private String name;
        private String code;
        private double confidence; // 置信度 0-1

        public ProblemType(String name, String code, double confidence) {
            this.name = name;
            this.code = code;
            this.confidence = confidence;
        }

        public boolean isSerious() {
            return confidence >= 0.7;
        }
    }
}
