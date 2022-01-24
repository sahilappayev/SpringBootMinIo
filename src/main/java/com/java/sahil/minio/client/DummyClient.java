package com.java.sahil.minio.client;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;

public class DummyClient {

    @SneakyThrows
    public static String getBase64Contract(){
        FileInputStream fis = new FileInputStream("src/main/resources/base64Contract.txt");
        return IOUtils.toString(fis, "UTF-8");
    }

    @SneakyThrows
    public static String getBase64Signature(){
        FileInputStream fis = new FileInputStream("src/main/resources/base64Signature.txt");
        return IOUtils.toString(fis, "UTF-8");
    }
}
