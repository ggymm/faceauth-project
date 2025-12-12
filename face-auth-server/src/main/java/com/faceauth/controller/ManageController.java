package com.faceauth.controller;

import com.faceauth.core.request.DataCreateReq;
import com.faceauth.core.request.DataPageReq;
import com.faceauth.core.request.DataUpdateReq;
import com.faceauth.core.response.Result;
import com.faceauth.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/face/manage")
@RequiredArgsConstructor
public class ManageController {

    private final ManageService service;

    @GetMapping("getPage")
    public Result<?> getPage(DataPageReq req) {
        return service.getPage(req);
    }

    @PostMapping("/createData")
    public Result<?> createData(DataCreateReq req) {
        return Result.ok(service.createData(req));
    }

    @PostMapping("/updateData")
    public Result<?> updateData(DataUpdateReq req) {
        return Result.ok(service.updateData(req));
    }
}
