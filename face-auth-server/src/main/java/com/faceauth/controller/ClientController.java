package com.faceauth.controller;

import com.faceauth.core.request.ClientRegisterReq;
import com.faceauth.core.request.ClientVerifyReq;
import com.faceauth.core.response.Result;
import com.faceauth.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/face/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @PostMapping("verify")
    public Result<?> verify(ClientVerifyReq req) {
        return service.verify(req);
    }

    @PostMapping("register")
    public Result<?> register(ClientRegisterReq req) {
        return service.register(req);
    }
}
