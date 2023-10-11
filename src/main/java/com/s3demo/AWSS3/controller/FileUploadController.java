package com.s3demo.AWSS3.controller;

import com.s3demo.AWSS3.service.S3FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final S3FileStorageService fileStorageService;

    @Autowired
    public FileUploadController(S3FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty. Please select a file to upload.");
        }

        String uploadedFileName = fileStorageService.uploadFile(file);

        return ResponseEntity.ok("File uploaded successfully. Unique file name: " + uploadedFileName);
    }
}
