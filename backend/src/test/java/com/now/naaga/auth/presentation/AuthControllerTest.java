package com.now.naaga.auth.presentation;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.presentation.dto.AuthRequest;
import com.now.naaga.auth.presentation.dto.AuthResponse;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthControllerTest extends CommonControllerTest {

    @MockBean
    private AuthClient authClient;

    @Autowired
    private PlayerBuilder playerBuilder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void 이미_존재하는_멤버_정보로_카카오_토큰을_통해서_로그인_요청을_하면_액세스_토큰을_발급한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        when(authClient.requestOauthInfo(any())).thenReturn(AuthInfo.of(player.getMember().getEmail(), player.getNickname()));

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
}
