package com.faceauth.controller;

import com.faceauth.core.request.FaceVerifyReq;
import com.faceauth.core.response.Result;
import com.faceauth.service.FaceVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/face/verify")
@RequiredArgsConstructor
public class FaceVerifyController {

    private final FaceVerifyService service;

    @PostMapping("client")
    public Result<?> verify(FaceVerifyReq req) {
        return service.verify(req);
    }
}
