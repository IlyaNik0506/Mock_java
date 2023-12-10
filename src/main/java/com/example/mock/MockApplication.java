package com.example.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class MockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockApplication.class, args);

        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();

        // Чтение конфига
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode configNode = objectMapper.readTree(new File("src/main/resources/config.json"));

            // сама заглушка и ее запуске
            for (JsonNode endpointNode : configNode) {
                String path = endpointNode.get("path").asText();
                String responseBody = endpointNode.get("responseBody").asText();

                WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(path))
                        .willReturn(WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

