package com.now.naaga.common;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
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

    protected RequestSpecification given(final Player player) {
        final Member member = player.getMember();
        final AuthToken generate = authTokenGenerator.generate(member, member.getId(), AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken);
    }

    protected RequestSpecification given(final Member member) {
        final AuthToken generate = authTokenGenerator.generate(member, member.getId(), AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken);
    }

    protected Long getIdFromLocationHeader(ExtractableResponse<Response> extractableResponse) {
        String[] split = extractableResponse.header("Location").split("/");
        return Long.parseLong(split[split.length - 1]);
    }
}
