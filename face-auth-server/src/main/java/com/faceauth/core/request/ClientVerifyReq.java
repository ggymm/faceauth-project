package com.faceauth.core.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ClientVerifyReq {
    private String userId;
    private MultipartFile faceImage;
}
