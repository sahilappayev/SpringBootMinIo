package com.java.sahil.minio.error;

public class ExtensionNotAcceptableException extends RuntimeException {
    public ExtensionNotAcceptableException(String extension) {
        super("." + extension + " ");
    }
}
