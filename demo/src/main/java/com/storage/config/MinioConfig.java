package com.storage.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.endPoint}")
    private String endpoint;
    @Value("${minio.username}")
    private String userName = "minioadmin";
    @Value("${minio.password}")
    private String password = "minioadmin";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(userName, password).build();
    }
}
