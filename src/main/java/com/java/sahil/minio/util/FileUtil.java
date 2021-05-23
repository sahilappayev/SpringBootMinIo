package com.java.sahil.minio.util;

import com.java.sahil.minio.error.ExtensionNotAcceptableException;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FileUtil {
    private final MinioClient minioClient;
    @Value("${file.upload.acceptableExtensions}")
    private String[] acceptableExtensions;

    private boolean isExtensionAcceptable(String extension) {
        for (String s : acceptableExtensions) {
            if (s.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    public String checkFileExtensionIsAcceptable(String contentType) {
        String extension = contentType.split("[/]")[1];
        if (isExtensionAcceptable(extension)) {
            return extension;
        } else {
            throw new ExtensionNotAcceptableException(extension);
        }
    }

    public String getFileExtensionIfAcceptable(@NotNull MultipartFile file) {
        String extension = file.getContentType().split("[/]")[1];
        if (isExtensionAcceptable(extension)) {
            return extension;
        } else {
            throw new ExtensionNotAcceptableException(extension);
        }
    }

    public String generateUniqueName(String extension) {
        Date date = new Date();
        return date.getTime() + "." + extension;
    }

}
