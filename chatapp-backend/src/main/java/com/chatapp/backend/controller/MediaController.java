package com.chatapp.backend.controller;

import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /** Upload an image — returns the URL to include in message */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = mediaService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("mediaUrl", url)));
    }
}
