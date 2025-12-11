package com.faceauth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.faceauth.core.model.ModelExtractResp;
import com.faceauth.core.request.FaceDataCreateReq;
import com.faceauth.core.request.FaceDataPageReq;
import com.faceauth.core.request.FaceDataUpdateReq;
import com.faceauth.core.response.PageResp;
import com.faceauth.core.response.Result;
import com.faceauth.entity.FaceData;
import com.faceauth.mapper.FaceDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceManageService {

    private final RestTemplate restTemplate;
    private final FaceDataMapper faceDataMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${model.service-url}")
    private String modelServiceUrl;

    public Result<?> getPage(FaceDataPageReq req) {
        final int page = req.getPage();
        final int size = req.getSize();
        final String userId = req.getUserId();

        QueryWrapper<FaceData> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNoneEmpty(userId), "user_id", userId);
        Page<FaceData> dataPage = faceDataMapper.selectPage(new Page<>(page, size), wrapper);

        PageResp<FaceData> resp = new PageResp<>();
        resp.setTotal(dataPage.getTotal());
        resp.setRecords(dataPage.getRecords());
        return Result.ok(resp);
    }

    public Result<?> createData(FaceDataCreateReq req) {
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            boolean success = uploadDirectory.mkdirs();
            if (!success) {
                return Result.error500("创建文件保存路径失败");
            }
        }
        final String userId = req.getUserId();
        final MultipartFile faceImage = req.getFaceImage();

        String imagePath;
        try {
            imagePath = saveFaceImage(userId, faceImage);
        } catch (Exception e) {
            return Result.error500(e.getMessage());
        }

        String featureVector;
        try {
            ModelExtractResp resp = extractFaceFeature(imagePath);
            if (resp.getError() != null) {
                return Result.error500(resp.getMessage());
            }

            ObjectMapper mapper = new ObjectMapper();
            featureVector = mapper.writeValueAsString(resp.getEmbedding());
        } catch (Exception e) {
            return Result.error500(e.getMessage());
        }

        FaceData faceData = new FaceData();
        faceData.setUserId(userId);
        faceData.setImageUrl(imagePath);
        faceData.setFeatureVector(featureVector);
        faceData.setCreateTime(System.currentTimeMillis());
        faceData.setUpdateTime(System.currentTimeMillis());

        faceDataMapper.insert(faceData);
        return Result.ok();
    }

    public Result<?> updateData(FaceDataUpdateReq req) {
        return Result.ok();
    }

    private String saveFaceImage(String userId, MultipartFile faceImage) throws Exception {
        final Path filepath = Paths.get(uploadDir, userId
                + "_" + UUID.randomUUID() + "." +
                FilenameUtils.getExtension(faceImage.getOriginalFilename()));
        Files.copy(faceImage.getInputStream(), filepath);
        return filepath.toString();
    }


    /**
     * 调用模型服务生成图像向量
     *
     * @param imagePath 图片文件路径
     * @return 人脸特征提取结果
     */
    private ModelExtractResp extractFaceFeature(String imagePath) throws Exception {
        // 读取图片文件
        Path path = Paths.get(imagePath);
        byte[] imageBytes = Files.readAllBytes(path);
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
}
