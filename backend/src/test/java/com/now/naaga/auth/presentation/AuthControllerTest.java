package com.now.naaga.auth.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.MemberAuthMapper;
import com.now.naaga.auth.infrastructure.dto.AuthInfo;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import com.now.naaga.auth.persistence.AuthRepository;
import com.now.naaga.auth.presentation.dto.AuthRequest;
import com.now.naaga.auth.presentation.dto.AuthResponse;
import com.now.naaga.auth.presentation.dto.RefreshTokenRequest;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Date;

import static com.now.naaga.auth.exception.AuthExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class AuthControllerTest extends CommonControllerTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private AuthRepository authRepository;

    @MockBean
    private AuthClient authClient;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void 이미_존재하는_멤버_정보로_카카오_토큰을_통해서_로그인_요청을_하면_액세스_토큰을_발급한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        final Player savedPlayer = playerRepository.save(player);

        when(authClient.requestOauthInfo(any())).thenReturn(AuthInfo.of(member.getEmail(), player.getNickname()));

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new AuthRequest("KAKAO", "1234"))
                .when()
                .post("/auth")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.CREATED.value();
        final AuthResponse actualResponse = extract.body().as(AuthResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse).isNotNull();
        });
    }

    @Test
    void 존재하지_않는_멤버_정보로_카카오_토큰을_통해서_로그인_요청을_하면_멤버와_플레이어_등록_후_액세스_토큰을_발급한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);

        when(authClient.requestOauthInfo(any())).thenReturn(AuthInfo.of(member.getEmail(), player.getNickname()));

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new AuthRequest("KAKAO", "1234"))
                .when()
                .post("/auth")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.CREATED.value();
        final AuthResponse actualResponse = extract.body().as(AuthResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse).isNotNull();
        });
    }


    @Test
    void 유효한_리프레시_토큰으로_만료된_액세스_토큰_발급_요청을_보낸_경우_새로_발급한_액세스_토큰과_리프레시_토큰을_반환한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        playerRepository.save(player);

        final long now = (new Date()).getTime();
        final Date accessTokenExpiredAt = new Date(now -1);
        final Date refreshTokenExpiredAt = new Date(now + 360000*24);
        final MemberAuth memberAuth = new MemberAuth(member.getId(), 1l, AuthType.KAKAO);
        final String convertedString = MemberAuthMapper.convertMemberAuthToString(memberAuth);
        final String expiredAccessToken = jwtProvider.generate(convertedString, accessTokenExpiredAt);
        final String validRefreshToken = jwtProvider.generate(convertedString, refreshTokenExpiredAt);

        authRepository.save(new AuthToken(expiredAccessToken, validRefreshToken, member));

        //when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new RefreshTokenRequest(validRefreshToken))
                .when()
                .post("/auth/refresh")
                .then()
                .log().all()
                .extract();

        //then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.CREATED.value();
        final AuthResponse actualResponse = extract.body().as(AuthResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse.accessToken()).isNotNull();
        });
    }

    @Test
    void 유효한_리프레시_토큰으로_만료되지_않은_액세스_토큰_발급_요청을_보낸_경우_리프레시_토큰을_폐기하고_예외를_발생한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        playerRepository.save(player);

        final long now = (new Date()).getTime();
        final Date accessTokenExpiredAt = new Date(now + 360000*24);
        final Date refreshTokenExpiredAt = new Date(now + 360000*24);
        final MemberAuth memberAuth = new MemberAuth(member.getId(), 1l, AuthType.KAKAO);
        final String convertedString = MemberAuthMapper.convertMemberAuthToString(memberAuth);
        final String validAccessToken = jwtProvider.generate(convertedString, accessTokenExpiredAt);
        final String validRefreshToken = jwtProvider.generate(convertedString, refreshTokenExpiredAt);

        authRepository.save(new AuthToken(validAccessToken, validRefreshToken, member));

        //when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new RefreshTokenRequest(validRefreshToken))
                .when()
                .post("/auth/refresh")
                .then()
                .log().all()
                .extract();

        //then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(INVALID_TOKEN_ACCESS.errorCode(),
                INVALID_TOKEN_ACCESS.errorMessage());

        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 리프레시_토큰이_만료된_경우_리프레시_토큰을_폐기하고_예외를_발생한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        playerRepository.save(player);

        final long now = (new Date()).getTime();
        final Date accessTokenExpiredAt = new Date(now -1 );
        final Date refreshTokenExpiredAt = new Date(now - 1);
        final String expiredAccessToken = jwtProvider.generate(member.getId().toString(), accessTokenExpiredAt);
        final String expiredRefreshToken = jwtProvider.generate(member.getId().toString(), refreshTokenExpiredAt);

        authRepository.save(new AuthToken(expiredAccessToken, expiredRefreshToken, member));

        //when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new RefreshTokenRequest(expiredRefreshToken))
                .when()
                .post("/auth/refresh")
                .then()
                .log().all()
                .extract();

        //then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(EXPIRED_TOKEN.errorCode(),
                EXPIRED_TOKEN.errorMessage());

        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 유효하지_않은_리프레시_토큰인_경우_예외를_발생한다() {
        //given & when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new RefreshTokenRequest("이상한 문자열"))
                .when()
                .post("/auth/refresh")
                .then()
                .log().all()
                .extract();

        //then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse expectedResponse = new ExceptionResponse(INVALID_TOKEN.errorCode(),
                INVALID_TOKEN.errorMessage());

        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);
        });
    }

    @Test
    void 액세스_토큰을_받아_회원_탈퇴를_진행한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        playerRepository.save(player);
        final AuthToken authToken = authTokenGenerator.generate(member, 1L, AuthType.KAKAO);
        authRepository.save(authToken);
        doNothing().when(authClient).requestUnlink(any());

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + authToken.getAccessToken())
                .when()
                .delete("/auth/unlink")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.NO_CONTENT.value();

        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
    }

    @Test
    void 액세스_토큰을_받아_로그아웃을_진행한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        playerRepository.save(player);
        final AuthToken authToken = authTokenGenerator.generate(member, 1L, AuthType.KAKAO);
        authRepository.save(authToken);
        doNothing().when(authClient).requestLogout(any());

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + authToken.getAccessToken())
                .when()
                .delete("/auth")
                .then()
                .log().all()
                .extract();

        // then
        final int actualStatusCode = extract.statusCode();
        final int expectedStatusCode = HttpStatus.NO_CONTENT.value();

        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
    }
}
