package com.now.naaga.common.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.common.exception.CommonExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
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
    
    //버전이 지정된 버킷 객체 삭제
    public void deleteFile(String imageUrl) {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/" + 1));
//        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));

//        String bucketVersionStatus = amazonS3.getBucketVersioningConfiguration(bucketName).getStatus();
//        if (!bucketVersionStatus.equals(BucketVersioningConfiguration.ENABLED)) {
//            System.out.printf("Bucket %s is not versioning-enabled.", bucketName);
//        } else {
//            // Add an object.
//            PutObjectResult putResult = amazonS3.putObject(bucketName, keyName, "Sample content for deletion example.");
//            System.out.printf("Object %s added to bucket %s\n", keyName, bucketName);
//
//            // Delete the version of the object that we just created.
//            System.out.println("Deleting versioned object " + keyName);
//            s3Client.deleteVersion(new DeleteVersionRequest(bucketName, keyName, putResult.getVersionId()));
//            System.out.printf("Object %s, version %s deleted\n", keyName, putResult.getVersionId());
//        }
//        S3ObjectSummary lastObjectSummary = amazonS3.listObjectsV2(bucketName, fileName)
//                                                  .getObjectSummaries().stream()
//                                                  .max(Comparator.comparing(S3ObjectSummary::getLastModified))
//                                                  .orElse(null);
//        if(lastObjectSummary != null) {
//            lastObjectSummary.getKey();
////            String latestVersionId = lastObjectSummary.get
//        }
    
        //코드날린 방법
//        VersionListing versionListing = amazonS3.listVersions(bucketName, fileName);
//        S3VersionSummary s3VersionSummary = versionListing.getVersionSummaries().stream()
//                                                          .max(Comparator.comparing(S3VersionSummary::getLastModified))
//                                                          .orElse(null);
//        String versionId = s3VersionSummary.getVersionId();
//        amazonS3.deleteVersion(new DeleteVersionRequest(bucketName, fileName, versionId));
    
        S3ObjectSummary s3ObjectSummary = amazonS3.listObjectsV2(bucketName, fileName)
                                                  .getObjectSummaries().stream()
                                                  .max(Comparator.comparing(S3ObjectSummary::getLastModified))
                                                  .orElse(null);
        String key = s3ObjectSummary.getKey();
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        //이것도 안되면 부순다
    
    }
}
