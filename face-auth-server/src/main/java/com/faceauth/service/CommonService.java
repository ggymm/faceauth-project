package com.faceauth.service;

import com.faceauth.core.model.ModelExtractResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    private final RestTemplate restTemplate;

    @Value("${model.service-url}")
    private String modelServiceUrl;

    /**
     * 从 File 提取人脸特征
     */
    public ModelExtractResp extractFaceFeature(File file) throws Exception {
        FileSystemResource fileResource = new FileSystemResource(file);
        return extractFaceFeatureInternal(fileResource);
    }

    /**
     * 从 MultipartFile 提取人脸特征（内存处理，无磁盘 I/O）
     */
    public ModelExtractResp extractFaceFeature(MultipartFile multipartFile) throws Exception {
        // 完全在内存中处理，不写入磁盘
        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        return extractFaceFeatureInternal(fileResource);
    }

    /**
     * 内部方法：发送 multipart/form-data 请求
     */
    private ModelExtractResp extractFaceFeatureInternal(Resource resource) {
        // 构建 multipart/form-data 请求体
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("file", resource);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 调用模型服务
        String url = modelServiceUrl + "/api/face/extract";
        return restTemplate.postForObject(url, entity, ModelExtractResp.class);
    }
}
