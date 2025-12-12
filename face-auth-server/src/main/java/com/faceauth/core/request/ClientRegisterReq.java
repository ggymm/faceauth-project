package com.faceauth.core.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ClientRegisterReq {
    private String userId;
    private MultipartFile faceImage;
}
