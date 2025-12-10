package com.faceauth.core.request;

import lombok.Data;

@Data
public class FaceDataCreateReq {
    private Long id;
    private String userId;
    private String faceImageBase64;
}
