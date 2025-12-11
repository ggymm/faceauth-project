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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceManageService {

    private final ObjectMapper objectMapper;
    private final CommonService commonService;
    private final FaceDataMapper faceDataMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
            ModelExtractResp resp = commonService.extractFaceFeature(new File(imagePath));
            if (resp.getError() != null) {
                return Result.error500(resp.getMessage());
            }

            featureVector = objectMapper.writeValueAsString(resp.getEmbedding());
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
}
