package com.faceauth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faceauth.core.request.FaceVerifyReq;
import com.faceauth.core.model.ModelExtractResp;
import com.faceauth.core.response.FaceVerifyResp;
import com.faceauth.core.response.Result;
import com.faceauth.entity.FaceData;
import com.faceauth.mapper.FaceDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceVerifyService {

    private final RestTemplate restTemplate;
    private final FaceDataMapper faceDataMapper;

    @Value("${model.service-url}")
    private String modelServiceUrl;

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
        String userId = req.getUserId();
        MultipartFile faceImage = req.getFaceImage();

        // 参数校验
        if (StringUtils.isEmpty(userId)) {
            return Result.error500("用户ID不能为空");
        }
        if (faceImage == null || faceImage.isEmpty()) {
            return Result.error500("人脸图片不能为空");
        }

        // 1. 查询数据库中该用户的人脸数据
        QueryWrapper<FaceData> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        FaceData faceData = faceDataMapper.selectOne(wrapper);

        if (faceData == null) {
            return Result.error500("该用户未注册人脸数据");
        }

        try {
            // 2. 直接从上传的图片提取人脸特征（不保存到磁盘）
            ModelExtractResp extractResp = extractFaceFeature(faceImage);
            if (extractResp.getError() != null) {
                log.error("提取人脸特征失败: {}", extractResp.getMessage());
                return Result.error500("提取人脸特征失败: " + extractResp.getMessage());
            }

            List<Double> currentEmbedding = extractResp.getEmbedding();
            log.info("待校验人脸特征提取成功，维度: {}, 检测置信度: {}",
                    currentEmbedding.size(), extractResp.getDetScore());

            // 3. 解析数据库中存储的人脸特征向量
            ObjectMapper mapper = new ObjectMapper();
            List<Double> registeredEmbedding = mapper.readValue(
                    faceData.getFeatureVector(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Double.class)
            );

            // 4. 计算余弦相似度
            double similarity = calculateCosineSimilarity(currentEmbedding, registeredEmbedding);
            log.info("人脸相似度: {}, 阈值: {}", similarity, SIMILARITY_THRESHOLD);

            // 5. 判断是否匹配
            boolean isMatch = similarity >= SIMILARITY_THRESHOLD;

            // 6. 构建响应
            FaceVerifyResp resp = new FaceVerifyResp();
            resp.setUserId(userId);
            resp.setIsMatch(isMatch);
            resp.setSimilarity(similarity);
            resp.setThreshold(SIMILARITY_THRESHOLD);
            resp.setMessage(isMatch ? "人脸校验成功" : "人脸校验失败，相似度不足");

            return Result.ok(resp);

        } catch (Exception e) {
            log.error("人脸校验处理失败", e);
            return Result.error500("人脸校验失败: " + e.getMessage());
        }
    }

    /**
     * 从MultipartFile直接提取人脸特征（不保存到磁盘）
     *
     * @param imageFile 人脸图片文件
     * @return 人脸特征提取结果
     */
    private ModelExtractResp extractFaceFeature(MultipartFile imageFile) throws Exception {
        // 直接读取上传文件的字节数组
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // 构建请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("image", base64Image);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        // 调用模型服务
        String url = modelServiceUrl + "/api/face/extract";
        log.info("调用模型服务提取人脸特征: {}", url);

        return restTemplate.postForObject(url, entity, ModelExtractResp.class);
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
