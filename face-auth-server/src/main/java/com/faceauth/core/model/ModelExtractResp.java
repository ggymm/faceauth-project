package com.faceauth.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 人脸特征提取响应
 * 对应模型服务 /api/face/extract 接口
 */
@Data
public class ModelExtractResp {

    /**
     * 特征向量（512维）
     */
    private List<Double> embedding;

    /**
     * 人脸边界框 [x1, y1, x2, y2]
     */
    private List<Double> bbox;

    /**
     * 检测置信度
     */
    @JsonProperty("det_score")
    private Double detScore;

    /**
     * 检测到的人脸数量
     */
    @JsonProperty("face_count")
    private Integer faceCount;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 详细消息
     */
    private String message;
}
