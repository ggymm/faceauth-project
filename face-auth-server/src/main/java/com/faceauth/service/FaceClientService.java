package com.faceauth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faceauth.core.model.ModelExtractResp;
import com.faceauth.core.request.FaceVerifyReq;
import com.faceauth.core.response.FaceVerifyResp;
import com.faceauth.core.response.Result;
import com.faceauth.entity.FaceData;
import com.faceauth.mapper.FaceDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceClientService {

    private final ObjectMapper objectMapper;
    private final CommonService commonService;
    private final FaceDataMapper faceDataMapper;

    /**
     * 人脸校验阈值（余弦相似度）
     * 通常InsightFace模型建议阈值在0.5-0.6之间
     */
    private static final double SIMILARITY_THRESHOLD = 0.6;

    /**
     * 校验人脸
     *
     * @param req 校验请求
     * @return 校验结果
     */
    public Result<?> verify(FaceVerifyReq req) {
        final String userId = req.getUserId();
        final MultipartFile faceImage = req.getFaceImage();

        if (StringUtils.isEmpty(userId)) {
            return Result.error500("用户 ID 不能为空");
        }
        if (faceImage == null || faceImage.isEmpty()) {
            return Result.error500("用户 人脸图片 不能为空");
        }

        QueryWrapper<FaceData> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.last("limit 1");
        FaceData faceData = faceDataMapper.selectOne(wrapper);

        if (faceData == null) {
            return Result.error500("用户 " + userId + " 未注册人脸数据");
        }

        List<Double> registeredEmbedding;
        try {
            registeredEmbedding = objectMapper.readValue(
                    faceData.getFeatureVector(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Double.class)
            );
        } catch (Exception e) {
            return Result.error500("读取用户 " + userId + " 注册人脸数据失败");
        }

        List<Double> currentEmbedding;
        try {
            ModelExtractResp resp = commonService.extractFaceFeature(faceImage);
            if (resp.getError() != null) {
                log.error("提取人脸特征失败: {}", resp.getMessage());
                return Result.error500("提取人脸特征失败: " + resp.getMessage());
            }
            currentEmbedding = resp.getEmbedding();
        } catch (Exception e) {
            return Result.error500("人脸校验失败: " + e.getMessage());
        }

        // 计算相似度
        double similarity = calculateCosineSimilarity(currentEmbedding, registeredEmbedding);
        log.info("人脸相似度: {}, 阈值: {}", similarity, SIMILARITY_THRESHOLD);

        // 响应数据
        FaceVerifyResp resp = new FaceVerifyResp();
        resp.setUserId(userId);
        resp.setIsMatch(similarity >= SIMILARITY_THRESHOLD);
        resp.setThreshold(SIMILARITY_THRESHOLD);
        resp.setSimilarity(similarity);
        return Result.ok(resp);
    }

    /**
     * 计算两个特征向量的余弦相似度
     *
     * @param embedding1 特征向量1
     * @param embedding2 特征向量2
     * @return 余弦相似度 [-1, 1]，值越大越相似
     */
    private double calculateCosineSimilarity(List<Double> embedding1, List<Double> embedding2) {
        if (embedding1.size() != embedding2.size()) {
            throw new IllegalArgumentException("特征向量维度不匹配");
        }

        // 计算点积
        double dotProduct = 0.0;
        for (int i = 0; i < embedding1.size(); i++) {
            dotProduct += embedding1.get(i) * embedding2.get(i);
        }

        // 计算向量模长
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < embedding1.size(); i++) {
            norm1 += embedding1.get(i) * embedding1.get(i);
            norm2 += embedding2.get(i) * embedding2.get(i);
        }
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        // 计算余弦相似度
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (norm1 * norm2);
    }
}
