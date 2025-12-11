package com.faceauth.core.response;

import lombok.Data;

/**
 * 人脸校验响应
 */
@Data
public class FaceVerifyResp {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否匹配
     */
    private Boolean isMatch;

    /**
     * 阈值
     */
    private Double threshold;

    /**
     * 相似度
     */
    private Double similarity;
}
