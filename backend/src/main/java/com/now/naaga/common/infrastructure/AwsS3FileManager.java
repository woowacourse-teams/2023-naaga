package com.now.naaga.common.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.common.exception.CommonExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AwsS3FileManager {
    
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    
    private final AmazonS3 amazonS3;
    
    private final String bucketName;
    
    public AwsS3FileManager(final AmazonS3 amazonS3,
                            @Value("${cloud.aws.s3.bucket}") final String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }
    
    public String uploadFile(final MultipartFile multipartFile) {
        validateFileExists(multipartFile);
        final String fileName = buildFileName(multipartFile.getOriginalFilename());
        final ObjectMetadata objectMetadata = initializeObjectMetadata(multipartFile);
        
        try (final InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        
        return amazonS3.getUrl(bucketName, fileName).toString();
    }
    
    private void validateFileExists(final MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new CommonException(CommonExceptionType.MULTIPART_FILE_NOT_EXIST);
        }
    }
    
    private String buildFileName(final String originalFileName) {
        final String extension = originalFileName.substring(originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR));
        return UUID.randomUUID() + extension;
    }
    
    private ObjectMetadata initializeObjectMetadata(final MultipartFile multipartFile) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }
    
    public void deleteFile(String imageUrl) {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}
