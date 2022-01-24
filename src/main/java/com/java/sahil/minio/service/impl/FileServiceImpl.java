package com.java.sahil.minio.service.impl;

import com.java.sahil.minio.service.FileService;
import com.java.sahil.minio.util.FileUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final MinioClient minioClient;
    private final FileUtil fileUtil;
    @Value("${minio.bucket}")
    private String bucketName;
    private final String VIDEO_MEDIA_TYPE = "video";
    private final String IMAGE_MEDIA_TYPE = "image";
    private final String PDF_MEDIA_TYPE = "pdf";

    @SneakyThrows
    @Override
    public byte[] getFile(String fileName, String folder) {
        String objectName = folder + fileName;
        GetObjectArgs minioRequest = GetObjectArgs.builder().bucket(bucketName).object(objectName).build();
        byte[] bytes = null;
        try {
            bytes = minioClient.getObject(minioRequest).readAllBytes();
        } catch (ErrorResponseException e) {
            ErrorResponse response = e.errorResponse();
            log.error("Minio error occurred with: {}, {}, {}",
                    kv("code", response.code()), kv("message", response.message()),
                    kv("objectName", response.objectName()));
        }
        return bytes;
    }

    @SneakyThrows
    @Override
    public String uploadImage(MultipartFile file, String folder, boolean isResize) {
        String fileExtension = fileUtil.getFileExtensionIfAcceptable(file, IMAGE_MEDIA_TYPE);
        String fileName = fileUtil.generateUniqueName(fileExtension);
        String objectName = folder + fileName;
        InputStream inputStream = file.getInputStream();

        if (isResize){
            BufferedImage image = ImageIO.read(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(fileUtil.resizeImage(image, image.getWidth(), image.getHeight()), fileExtension, byteArrayOutputStream);
            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                        inputStream, inputStream.available(), -1)
                .contentType(file.getContentType())
                .build());
        return fileName;
    }


    @SneakyThrows
    @Override
    public void deleteFile(String fileName, String folder) {
        String objectName = folder + fileName;
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    @SneakyThrows
    @Override
    public String uploadVideo(MultipartFile file, String folder) {
        String fileExtension = fileUtil.getFileExtensionIfAcceptable(file, VIDEO_MEDIA_TYPE);
        String fileName = fileUtil.generateUniqueName(fileExtension);
        String objectName = folder + fileName;
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                        file.getInputStream(), file.getInputStream().available(), -1)
                .contentType(file.getContentType())
                .build());
        return fileName;
    }

    @SneakyThrows
    @Override
    public String uploadPdf(MultipartFile file, String folder) {
        String fileExtension = fileUtil.getFileExtensionIfAcceptable(file, PDF_MEDIA_TYPE);
        String fileName = fileUtil.generateUniqueName(fileExtension);
        String objectName = folder + fileName;
        InputStream inputStream = file.getInputStream();
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                        inputStream, inputStream.available(), -1)
                .contentType(file.getContentType())
                .build());
        return fileName;
    }

    @SneakyThrows
    public String uploadInputStreamImage(InputStream file, String folder) {
        String fileName = fileUtil.generateUniqueName(fileUtil.getFileExtensionFromInputStream(file));
        String fileExtension = fileUtil.getFileExtensionIfAcceptable(fileName, IMAGE_MEDIA_TYPE);
        Path path = new File(fileName).toPath();
        String mimeType = Files.probeContentType(path);
        fileName = fileUtil.generateUniqueName(fileExtension);
        String objectName = folder + fileName;

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                        file, file.available(), -1)
                .contentType(mimeType)
                .build());
        return fileName;
    }

}
