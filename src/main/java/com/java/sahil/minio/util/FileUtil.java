package com.java.sahil.minio.util;

import com.java.sahil.minio.error.ExtensionNotAcceptableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUtil {
    @Value("${file.upload.acceptableVideoExtensions}")
    private String[] acceptableVideoExtensions;
    @Value("${file.upload.acceptableImageExtensions}")
    private String[] acceptableImageExtensions;
    @Value("${file.upload.acceptablePdfExtensions}")
    private String[] acceptablePdfExtensions;

    private boolean isExtensionAcceptable(String extension, String mediaType) {
        if (mediaType.equals("image")) {
            for (String s : acceptableImageExtensions) {
                if (s.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        } else if (mediaType.equals("video")) {
            for (String s : acceptableVideoExtensions) {
                if (s.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        } else if (mediaType.equals("pdf")) {
            for (String s : acceptablePdfExtensions) {
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

    public String getFileExtensionIfAcceptable(String fileName, String mediaType) {
        String extension = fileName.split("\\.")[1];
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

    public String getFileExtensionFromInputStream(InputStream inputStream) {
        String fileExtension = null;
        try {
            String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
            String[] tokens = mimeType.split("[/]");
            fileExtension = tokens[1];
        } catch (IOException ioException) {
            log.error("Error occurred: {}", ioException.getMessage());
        }
        return fileExtension;
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

}
