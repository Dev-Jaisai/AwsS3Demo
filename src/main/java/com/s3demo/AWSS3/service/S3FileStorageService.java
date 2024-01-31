package com.s3demo.AWSS3.service;

import com.s3demo.AWSS3.dto.ResponseMetaData;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3FileStorageService.class);

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public S3FileStorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public ResponseMetaData uploadFile(MultipartFile file) {
        try {
            if (Objects.isNull(file.getOriginalFilename())) {
                throw new IllegalArgumentException("File is empty");
            }

            File convertedFile = convertMultiPartToFile(file);
            String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, convertedFile);
            amazonS3.putObject(putObjectRequest);

            ResponseMetaData responseMetaData = ResponseMetaData.builder()
                    .fielName(uniqueFileName)
                    .fileCreated(DateTime.now().toString())
                    .fileSize(String.valueOf(file.getSize()))
                    .build();

            // Clean up the converted file if needed
            if (!convertedFile.delete()) {
                logger.warn("Failed to delete the temporary file: {}", convertedFile.getAbsolutePath());
            }

            return responseMetaData;
        } catch (IOException e) {
            logger.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String timestamp = String.valueOf(System.currentTimeMillis()); // Get current timestamp
        return UUID.randomUUID().toString() + timestamp + extension;
    }

    public boolean deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(bucketName, fileName);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete file from S3", e);
            return false;
        }
    }
}
