package com.faceauth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.faceauth.core.request.FaceDataCreateReq;
import com.faceauth.model.FaceData;
import com.faceauth.mapper.FaceDataMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class FaceDataService {

    @Resource
    private FaceDataMapper faceDataMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;


    public List<FaceData> getAll() {
        return faceDataMapper.selectList(null);
    }

    public Page<FaceData> getPage(int pageNum, int pageSize) {
        Page<FaceData> page = new Page<>(pageNum, pageSize);
        return faceDataMapper.selectPage(page, null);
    }

    public FaceData getById(Long id) {
        return faceDataMapper.selectById(id);
    }

    public FaceData create(FaceDataCreateReq request) {
        String imageUrl = saveBase64Image(request.getFaceImageBase64());

        FaceData faceData = new FaceData();
        faceData.setUserId(request.getUserId());
        faceData.setImageUrl(imageUrl);
        faceData.setCreateTime(LocalDateTime.now());
        faceData.setUpdateTime(LocalDateTime.now());

        faceDataMapper.insert(faceData);
        return faceData;
    }

    public FaceData update(FaceDataCreateReq request) {
        FaceData faceData = faceDataMapper.selectById(request.getId());
        if (faceData == null) {
            throw new RuntimeException("人脸数据不存在");
        }

        if (request.getFaceImageBase64() != null && !request.getFaceImageBase64().isEmpty()) {
            deleteFile(faceData.getImageUrl());
            String imageUrl = saveBase64Image(request.getFaceImageBase64());
            faceData.setImageUrl(imageUrl);
        }

        if (request.getUserId() != null) {
            faceData.setUserId(request.getUserId());
        }

        faceData.setUpdateTime(LocalDateTime.now());
        faceDataMapper.updateById(faceData);
        return faceData;
    }

    public void delete(Long id) {
        FaceData faceData = faceDataMapper.selectById(id);
        if (faceData != null) {
            deleteFile(faceData.getImageUrl());
            faceDataMapper.deleteById(id);
        }
    }

    private String saveBase64Image(String base64String) {
        try {
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            String base64Data = base64String;
            String extension = ".jpg";

            if (base64String.contains(",")) {
                String[] parts = base64String.split(",");
                if (parts.length > 1) {
                    base64Data = parts[1];
                    String mimeType = parts[0];
                    if (mimeType.contains("png")) {
                        extension = ".png";
                    } else if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
                        extension = ".jpg";
                    } else if (mimeType.contains("gif")) {
                        extension = ".gif";
                    }
                }
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, imageBytes);

            return "/uploads/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("文件保存失败", e);
        }
    }

    private void deleteFile(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
            try {
                String filename = imageUrl.substring("/uploads/".length());
                Path filePath = Paths.get(uploadDir, filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't throw exception
            }
        }
    }
}
