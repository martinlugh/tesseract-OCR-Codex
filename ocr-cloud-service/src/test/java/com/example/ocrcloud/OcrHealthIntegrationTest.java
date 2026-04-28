package com.example.ocrcloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OcrHealthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpointShouldRespond() {
        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:" + port + "/api/ocr/health", String.class);
        assertTrue(resp.getStatusCode().is2xxSuccessful() || resp.getStatusCode().is4xxClientError());
    }
}
