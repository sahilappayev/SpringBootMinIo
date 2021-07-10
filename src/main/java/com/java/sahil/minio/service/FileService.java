package com.java.sahil.minio.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    byte[] getFile(String fileName, String folder);

    String uploadImage(MultipartFile file, String folder);

    void deleteFile(String fileName, String folder);

    String uploadVideo(MultipartFile file, String folder);

}
