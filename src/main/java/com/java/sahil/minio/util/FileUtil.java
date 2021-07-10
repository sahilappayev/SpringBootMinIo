package com.java.sahil.minio.util;

import com.java.sahil.minio.error.ExtensionNotAcceptableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FileUtil {
    @Value("${file.upload.acceptableVideoExtensions}")
    private String[] acceptableVideoExtensions;
    @Value("${file.upload.acceptableImageExtensions}")
    private String[] acceptableImageExtensions;

    private boolean isExtensionAcceptable(String extension, String mediaType) {
        if (mediaType.equals("image")){
            for (String s : acceptableImageExtensions) {
                if (s.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        }else if (mediaType.equals("video")){
            for (String s : acceptableVideoExtensions) {
                if (s.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getFileExtensionIfAcceptable(@NotNull MultipartFile file, String mediaType) {
        String extension = file.getContentType().split("[/]")[1];
        if (isExtensionAcceptable(extension, mediaType)) {
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
