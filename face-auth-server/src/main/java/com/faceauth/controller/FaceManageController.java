package com.faceauth.controller;

import com.faceauth.core.request.FaceDataCreateReq;
import com.faceauth.core.request.FaceDataPageReq;
import com.faceauth.core.request.FaceDataUpdateReq;
import com.faceauth.core.response.Result;
import com.faceauth.service.FaceManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/face/manage")
@RequiredArgsConstructor
public class FaceManageController {

    private final FaceManageService service;

    @GetMapping("getPage")
    public Result<?> getPage(FaceDataPageReq req) {
        return service.getPage(req);
    }

    @PostMapping("/createData")
    public Result<?> createData(FaceDataCreateReq req) {
        return Result.ok(service.createData(req));
    }

    @PostMapping("/updateData")
    public Result<?> updateData(FaceDataUpdateReq req) {
        return Result.ok(service.updateData(req));
    }
}
