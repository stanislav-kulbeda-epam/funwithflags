package com.epam.funwithflags.service;

import com.epam.funwithflags.config.DataProperties;
import com.epam.funwithflags.model.Route;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

class ReactiveRouteServiceTest {

    public static MockWebServer mockBackEnd1;
    public static MockWebServer mockBackEnd2;
    private ReactiveRouteService reactiveRouteService;

    Resource flights1File = new ClassPathResource("flights1.json");
    Resource flights2File = new ClassPathResource("flights2.json");

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd1 = new MockWebServer();
        mockBackEnd1.start();
        mockBackEnd2 = new MockWebServer();
        mockBackEnd2.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd1.shutdown();
        mockBackEnd2.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl1 = String.format("http://localhost:%s/flights1",
                mockBackEnd1.getPort());
        String baseUrl2 = String.format("http://localhost:%s/flights2",
                mockBackEnd2.getPort());
        DataProperties dataProperties = new DataProperties();
        dataProperties.setProviders(Arrays.asList(baseUrl1, baseUrl2));
        reactiveRouteService = new ReactiveRouteService(dataProperties);
    }

    @Test
    void getRoutes_merged() throws IOException {

        mockBackEnd1.enqueue(new MockResponse()
                .setBody(flights1File.getContentAsString(Charset.defaultCharset()))
                .addHeader("Content-Type", "application/json"));
        mockBackEnd2.enqueue(new MockResponse()
                .setBody(flights2File.getContentAsString(Charset.defaultCharset()))
                .addHeader("Content-Type", "application/json"));
        Flux<Route> routes = reactiveRouteService.getRoutes();

        StepVerifier.create(routes).expectNextCount(60000)
                .verifyComplete();
    }

    @Test
    void getRoutes_oneFailed() throws IOException {

        mockBackEnd1.enqueue(new MockResponse()
                .setBody(flights1File.getContentAsString(Charset.defaultCharset()))
                .addHeader("Content-Type", "application/json"));
        mockBackEnd2.enqueue(new MockResponse().setResponseCode(404));
        Flux<Route> routes = reactiveRouteService.getRoutes();

        StepVerifier.create(routes).expectNextCount(30000)
                .verifyComplete();
    }
}