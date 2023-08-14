package com.now.naaga.auth.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.now.naaga.player.exception.PlayerExceptionType.PLAYER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class AuthArgumentResolverTest extends CommonControllerTest {

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
    void 인증_헤더의_토큰_정보가_존재하지_않는_멤버일_때_예외를_발생한다() {
        // given
        final Player player = playerRepository.save(new Player("delete", new Score(0), new Member("another@woowa.com")));
        final Long id = player.getMember().getId();
        playerRepository.delete(player);
        final AuthToken authToken = authTokenGenerator.generate(id);
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
        final int expectedStatusCode = PLAYER_NOT_FOUND.httpStatus().value();
        final ExceptionResponse actualResponse = extract.body().as(ExceptionResponse.class);
        final ExceptionResponse exceptedResponse = new ExceptionResponse(PLAYER_NOT_FOUND.errorCode(), PLAYER_NOT_FOUND.errorMessage());
        assertSoftly(softly -> {
            softly.assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
            softly.assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(exceptedResponse);
        });
    }

    @Test
    void 인증_헤더의_토큰_정보가_존재하는_멤버일_때_정상응답한다() {
        // given
        final Player player = playerRepository.save(new Player("delete", new Score(0), new Member("another@woowa.com")));
        final Long id = player.getMember().getId();
        final AuthToken authToken = authTokenGenerator.generate(id);
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
