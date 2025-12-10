package com.faceauth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.faceauth.core.request.FaceDataCreateReq;
import com.faceauth.model.FaceData;
import com.faceauth.service.FaceDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/face/data")
@CrossOrigin(origins = "*")
public class FaceDataController {

    private final FaceDataService faceDataService;

    public FaceDataController(FaceDataService faceDataService) {
        this.faceDataService = faceDataService;
    }

    @GetMapping
    public ResponseEntity<List<FaceData>> getAll() {
        return ResponseEntity.ok(faceDataService.getAll());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FaceData>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(faceDataService.getPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaceData> getById(@PathVariable Long id) {
        FaceData faceData = faceDataService.getById(id);
        if (faceData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faceData);
    }

    @PostMapping("/create")
    public ResponseEntity<FaceData> create(@RequestBody FaceDataCreateReq request) {
        try {
            FaceData faceData = faceDataService.create(request);
            return ResponseEntity.ok(faceData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<FaceData> update(@RequestBody FaceDataCreateReq request) {
        try {
            FaceData faceData = faceDataService.update(request);
            return ResponseEntity.ok(faceData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody FaceDataCreateReq request) {
        try {
            faceDataService.delete(request.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
