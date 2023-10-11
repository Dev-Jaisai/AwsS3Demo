package com.s3demo.AWSS3.service;

import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.ObjectMetadata;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class S3FileStorageService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public S3FileStorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file) {
        try {
            File convertedFile = convertMultiPartToFile(file);
            String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, convertedFile);
            amazonS3.putObject(putObjectRequest);

            // Clean up the converted file if needed
            convertedFile.delete();

            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
            File file1 = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(file1);
            fos.write(file.getBytes());
            fos.close();
            return file1;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String timestamp = String.valueOf(System.currentTimeMillis()); // Get current timestamp
        String uniqueFileName = UUID.randomUUID().toString() + timestamp + extension;
        return uniqueFileName;
    }

}
