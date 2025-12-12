package com.faceauth.core.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DataUpdateReq {
    private Long id;
    private String userId;
    private MultipartFile faceImage;
}
