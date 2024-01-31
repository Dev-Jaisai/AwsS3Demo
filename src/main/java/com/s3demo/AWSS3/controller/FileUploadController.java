package com.s3demo.AWSS3.controller;

import com.s3demo.AWSS3.dto.ResponseMetaData;
import com.s3demo.AWSS3.service.S3FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileUploadController {

    private final S3FileStorageService fileStorageService;

    @Autowired
    public FileUploadController(S3FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMetaData> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.info("file is empty "+file.getOriginalFilename());

            return new ResponseEntity<>(ResponseMetaData.builder().msg("File is empty").build(), HttpStatus.BAD_REQUEST);
        }

        try {
            log.info("file is now ready to upload "+file.getOriginalFilename());
            ResponseMetaData responseMetaData = fileStorageService.uploadFile(file);
            return ResponseEntity.ok(responseMetaData);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(ResponseMetaData.builder().msg("Failed to upload file: " + e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        boolean delete = fileStorageService.deleteFile(fileName);
        if (delete) {
            return ResponseEntity.ok("File '" + fileName + "' has been deleted from the S3 bucket.");
        } else {
            return ResponseEntity.notFound().build();

        }
    }
}
