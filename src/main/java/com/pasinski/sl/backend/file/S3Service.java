package com.pasinski.sl.backend.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    @Value("${AWSBucketName}")
    String bucketName;

    public S3Service(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public void uploadFile(String fileName, FileType type, File file) {
        PutObjectRequest request = new PutObjectRequest(bucketName, type.getPath() + fileName, file);
        amazonS3Client.putObject(request);
    }

    public void uploadInputStream(String fileName, FileType type, InputStream inputStream) {
        PutObjectRequest request = new PutObjectRequest(bucketName, type.getPath() + fileName, inputStream, new ObjectMetadata());
        amazonS3Client.putObject(request);
    }

    public void deleteFile(String fileName, FileType type) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, type.getPath() + fileName));
    }

    public String getFileUrl(String fileName, FileType fileType) {
        return amazonS3Client.getUrl(bucketName, fileType.getPath() + fileName).toString();
    }
}
