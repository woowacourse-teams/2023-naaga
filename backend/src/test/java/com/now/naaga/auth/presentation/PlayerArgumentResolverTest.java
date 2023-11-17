package com.now.naaga.auth.presentation;

import static com.now.naaga.player.exception.PlayerExceptionType.PLAYER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.common.ControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class PlayerArgumentResolverTest extends ControllerTest {

    @Test
    void 인증_헤더의_토큰_정보가_존재하지_않는_멤버일_때_예외를_발생한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        playerRepository.delete(player);

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
        final Player player = playerBuilder.init()
                                           .build();

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

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
