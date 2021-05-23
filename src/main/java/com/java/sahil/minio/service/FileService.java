package com.java.sahil.minio.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    byte[] getPhoto(String fileName, String folder);

    String uploadPhoto(MultipartFile file, String folder);

    void deletePhoto(String fileName, String folder);

}
