package com.example.ocrcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OcrCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcrCloudApplication.class, args);
    }
}
