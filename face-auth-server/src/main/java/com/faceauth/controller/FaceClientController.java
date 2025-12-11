package com.faceauth.controller;

import com.faceauth.core.request.FaceVerifyReq;
import com.faceauth.core.response.Result;
import com.faceauth.service.FaceClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/face/client")
@RequiredArgsConstructor
public class FaceClientController {

    private final FaceClientService service;

    @PostMapping("verify")
    public Result<?> verify(FaceVerifyReq req) {
        return service.verify(req);
    }
}
