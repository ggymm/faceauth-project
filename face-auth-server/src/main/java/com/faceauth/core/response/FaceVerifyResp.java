package com.faceauth.core.response;

import lombok.Data;

/**
 * 人脸校验响应
 */
@Data
public class FaceVerifyResp {

    /**
     * 是否匹配
     */
    private Boolean isMatch;

    /**
     * 相似度
     */
    private Double similarity;

    /**
     * 阈值
     */
    private Double threshold;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 消息
     */
    private String message;
}
