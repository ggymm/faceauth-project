package com.faceauth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.faceauth.core.model.ModelExtractResp;
import com.faceauth.core.request.DataCreateReq;
import com.faceauth.core.request.DataPageReq;
import com.faceauth.core.request.DataUpdateReq;
import com.faceauth.core.response.PageResp;
import com.faceauth.core.response.Result;
import com.faceauth.entity.FaceData;
import com.faceauth.mapper.FaceDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageService {

    private final ObjectMapper objectMapper;
    private final CommonService commonService;
    private final FaceDataMapper faceDataMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Result<?> getPage(DataPageReq req) {
        final int page = req.getPage();
        final int size = req.getSize();
        final String userId = req.getUserId();

        LambdaQueryWrapper<FaceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNoneEmpty(userId), FaceData::getUserId, userId);
        Page<FaceData> dataPage = faceDataMapper.selectPage(new Page<>(page, size), wrapper);

        PageResp<FaceData> resp = new PageResp<>();
        resp.setTotal(dataPage.getTotal());
        resp.setRecords(dataPage.getRecords());
        return Result.ok(resp);
    }

    public Result<?> createData(DataCreateReq req) {
        final File uploadDirectory = new File(uploadDir);
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
            imagePath = commonService.saveFaceImage(userId, faceImage);
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
        faceDataMapper.insert(faceData);
        return Result.ok();
    }

    public Result<?> updateData(DataUpdateReq req) {
        return Result.ok(req);
    }
}
