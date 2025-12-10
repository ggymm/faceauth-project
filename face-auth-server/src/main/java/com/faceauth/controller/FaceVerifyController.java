package com.faceauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face/verify")
@CrossOrigin(origins = "*")
public class FaceVerifyController {

    @PostMapping
    public ResponseEntity<String> verify() {
        // TODO: 实现人脸验证功能
        return ResponseEntity.ok("Face verify endpoint - to be implemented");
    }
}
