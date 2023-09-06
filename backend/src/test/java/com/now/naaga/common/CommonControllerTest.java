package com.now.naaga.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class CommonControllerTest {

    @LocalServerPort
    private int port;

    protected void setUp() {
        RestAssured.port = port;
    }

    protected Long getIdFromLocationHeader(ExtractableResponse<Response> extractableResponse) {
        String[] split = extractableResponse.header("Location").split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
