package com.java.sahil.minio;

import com.java.sahil.minio.client.DummyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author SahilAppayev
 * @since 23.05.2021
 */

@SpringBootApplication
//@OpenAPIDefinition(info = @Info(title = "SpringBoot - MinIO", version = "1.0",
//        description = "Spring Boot App With MinIO File Storage Server"))
public class SpringBootMinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMinioApplication.class, args);
    }

}
