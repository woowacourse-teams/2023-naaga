package com.now.naaga.auth.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.fixture.PlayerFixture;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.now.naaga.auth.exception.AuthExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class AuthInterceptorTest extends CommonControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    protected void setUp(){
        super.setUp();
    }

    @Test
    void 인증_헤더_정보가_존재하지_않을_때_401_응답한다() {
        // given & when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .when()
                .get("/statistics/my")
                .then()
                .log().all()
                .extract();
        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = NOT_EXIST_HEADER.httpStatus().value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(NOT_EXIST_HEADER.errorCode(), NOT_EXIST_HEADER.errorMessage());
        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 인증_헤더_정보가_Bearer가_아닐_때_401_응답한다() {
        // given
        final Member savedMember = memberRepository.save(new Member("chae@chae.com"));

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .auth().preemptive().basic(savedMember.getEmail(), "1111")
                .when()
                .get("/statistics/my")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = INVALID_HEADER.httpStatus().value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(INVALID_HEADER.errorCode(), INVALID_HEADER.errorMessage());
        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 인증_헤더의_토큰_정보가_유효하지_않을_때_401_응답한다() {
        // given
        final Member savedMember = memberRepository.save(new Member("chae@chae.com"));
        final AuthToken authToken = authTokenGenerator.generate(savedMember.getId(), 1L, AuthType.KAKAO);
        final String accessToken = "이상한문자열" + authToken.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/statistics/my")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = INVALID_TOKEN.httpStatus().value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(INVALID_TOKEN.errorCode(), INVALID_TOKEN.errorMessage());
        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 인증_헤더의_토큰_정보가_존재하지_않는_멤버일_때_401_응답한다() {
        // given
        final Member savedMember = memberRepository.save(new Member("chae@chae.com"));
        final AuthToken authToken = authTokenGenerator.generate(savedMember.getId(), 1L, AuthType.KAKAO);
        final String accessToken = "이상한문자열" + authToken.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/statistics/my")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = INVALID_TOKEN.httpStatus().value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(INVALID_TOKEN.errorCode(), INVALID_TOKEN.errorMessage());
        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 인증_헤더의_토큰_정보가_존재하는_멤버일_때_정상응답한다() {
        // given
        final Player player = PlayerFixture.PLAYER();
        final Player savedPlayer = playerRepository.save(player);
        final AuthToken authToken = authTokenGenerator.generate(savedPlayer.getMember().getId(), 1L, AuthType.KAKAO);
        final String accessToken = authToken.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/statistics/my")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.OK.value();
        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
    }
}
