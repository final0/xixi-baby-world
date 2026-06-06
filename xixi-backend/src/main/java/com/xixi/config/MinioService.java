package com.xixi.config;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig.MinioProperties minioProperties;

    public String upload(MultipartFile file, String directory) throws Exception {
        String ext = getExtension(file.getOriginalFilename());
        String objectName = directory + "/" + UUID.randomUUID() + "." + ext;
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        return objectName;
    }

    public String uploadBytes(byte[] bytes, String objectName, String contentType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                .contentType(contentType)
                .build());
        return objectName;
    }

    public String uploadThumbnail(MultipartFile file, String originalPath) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream()).size(400, 400).keepAspectRatio(true).toOutputStream(baos);
        String thumbPath = originalPath.replace("/photos/", "/thumbnails/");
        uploadBytes(baos.toByteArray(), thumbPath, "image/jpeg");
        return thumbPath;
    }

    public String getFileUrl(String objectName) {
        if (objectName == null) return null;
        return minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + objectName;
    }

    public String getPresignedUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .method(Method.GET)
                .expiry(1, TimeUnit.HOURS)
                .build());
    }

    public void delete(String objectName) {
        if (objectName == null) return;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName()).object(objectName).build());
        } catch (Exception e) {
            log.warn("MinIO 文件删除失败: {}", objectName, e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "jpg";
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1).toLowerCase() : "jpg";
    }
}
