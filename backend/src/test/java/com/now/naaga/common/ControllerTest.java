package com.now.naaga.common;

import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ControllerTest extends AbstractTest {

    @Autowired
    protected AuthTokenGenerator authTokenGenerator;

    @Autowired
    protected JwtProvider jwtProvider;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected Long getIdFromLocationHeader(ExtractableResponse<Response> extractableResponse) {
        String[] split = extractableResponse.header("Location").split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
