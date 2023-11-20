package com.now.naaga.player.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.common.MySqlContainerControllerTest;
import com.now.naaga.common.exception.CommonExceptionType;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.EditPlayerRequest;
import com.now.naaga.player.presentation.dto.EditPlayerResponse;
import com.now.naaga.player.presentation.dto.PlayerResponse;
import com.now.naaga.player.presentation.dto.RankResponse;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
public class PlayerControllerTest extends MySqlContainerControllerTest {

    @Test
    void 닉네임을_변경한다() {
        // given
        final String nickname = "변경된 닉네임";
        final Player player = playerBuilder.init().build();
        final EditPlayerRequest editPlayerRequest = new EditPlayerRequest(nickname);

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .body(editPlayerRequest)
                .when().patch("/profiles/my")
                .then().log().all()
                .extract();

        // then
        final EditPlayerResponse expected = new EditPlayerResponse(nickname);
        final EditPlayerResponse actual = response.as(EditPlayerResponse.class);
        final int statusCode = response.statusCode();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            softAssertions.assertThat(statusCode)
                    .isEqualTo(HttpStatus.OK.value());
        });
    }

    @Test
    void 잘못된_닉네임을_입력하면_예외를_응답한다() {
        // given
        final String nickname = "변경된 닉네임!!";
        final Player player = playerBuilder.init().build();
        final EditPlayerRequest editPlayerRequest = new EditPlayerRequest(nickname);

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .body(editPlayerRequest)
                .when().patch("/profiles/my")
                .then().log().all()
                .extract();

        // then
        final CommonExceptionType exceptionType = CommonExceptionType.INVALID_REQUEST_BODY;
        final ExceptionResponse expected = new ExceptionResponse(exceptionType.errorCode(), exceptionType.errorMessage());
        final ExceptionResponse actual = response.as(ExceptionResponse.class);
        final int statusCode = response.statusCode();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            softAssertions.assertThat(statusCode)
                    .isEqualTo(HttpStatus.BAD_REQUEST.value());
        });
    }


    @Test
    void 플레이어_정보를_정상적으로_조회한다() {
        //given
        final Player player = playerBuilder.init()
                                           .build();

        //when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", authorizationForBearer(player))
                .contentType(ContentType.JSON)
                .when()
                .get("/profiles/my")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final PlayerResponse actual = extract.as(PlayerResponse.class);
        final PlayerResponse expected = PlayerResponse.from(player);

        assertSoftly(softAssertions -> {
                    softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(actual)
                                  .usingRecursiveComparison()
                                  .isEqualTo(expected);
                }
        );
    }

    @Test
    void 멤버의_랭크를_조회한다() {
        // given
        final Player player = playerBuilder.init()
                                           .totalScore(new Score(15))
                                           .build();

        playerBuilder.init()
                     .totalScore(new Score(20))
                     .build();

        playerBuilder.init()
                     .totalScore(new Score(30))
                     .build();
        // when
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", authorizationForBearer(player))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/ranks/my")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final RankResponse rankResponse = response.as(RankResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(rankResponse.getPlayer().getId()).isEqualTo(player.getId());
            softly.assertThat(rankResponse.getPlayer().getNickname()).isEqualTo(player.getNickname());
            softly.assertThat(rankResponse.getPlayer().getTotalScore()).isEqualTo(player.getTotalScore().getValue());
            softly.assertThat(rankResponse.getRank()).isEqualTo(3);
            softly.assertThat(rankResponse.getPercentage()).isEqualTo(100);
        });
    }

    @Test
    void 모든_맴버의_랭크를_조회한다() {
        // given
        final Player player1 = playerBuilder.init()
                                            .totalScore(new Score(15))
                                            .build();

        final Player player2 = playerBuilder.init()
                                            .totalScore(new Score(20))
                                            .build();

        final Player player3 = playerBuilder.init()
                                            .totalScore(new Score(30))
                                            .build();

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                                                                  .log().all()
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .when().get("/ranks?sort-by=rank&order=ascending")
                                                                  .then().log().all()
                                                                  .statusCode(HttpStatus.OK.value())
                                                                  .extract();

        // then
        final List<RankResponse> rankResponseList = response.as(new TypeRef<>() {
        });
        assertThat(rankResponseList).hasSize(3);
        final RankResponse firstRank = rankResponseList.get(0);
        assertThat(firstRank.getPlayer().getNickname()).isEqualTo(player3.getNickname());
        assertThat(firstRank.getRank()).isEqualTo(1);

        final RankResponse secondRank = rankResponseList.get(1);
        assertThat(secondRank.getPlayer().getNickname()).isEqualTo(player2.getNickname());
        assertThat(secondRank.getRank()).isEqualTo(2);

        final RankResponse thirdRank = rankResponseList.get(2);
        assertThat(thirdRank.getPlayer().getNickname()).isEqualTo(player1.getNickname());
        assertThat(thirdRank.getRank()).isEqualTo(3);
    }

    @Test
    void 모든_맴버의_랭크를_조회할때_요청_파라미터가_없으면_예외를_발생시킨다() {
        // given
        final Player player1 = playerBuilder.init()
                                            .totalScore(new Score(15))
                                            .build();

        final Player player2 = playerBuilder.init()
                                            .totalScore(new Score(20))
                                            .build();

        final Player player3 = playerBuilder.init()
                                            .totalScore(new Score(30))
                                            .build();

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                                                                  .log().all()
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .when().get("/ranks?sort-by=rank&order=decending")
                                                                  .then().log().all()
                                                                  .extract();

        // then
        final int statusCode = response.statusCode();
        final ExceptionResponse actual = response.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(INVALID_REQUEST_PARAMETERS.errorCode(), INVALID_REQUEST_PARAMETERS.errorMessage());

        assertSoftly(softAssertions -> {
                         softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
                         softAssertions.assertThat(actual)
                                       .usingRecursiveComparison()
                                       .ignoringExpectedNullFields()
                                       .ignoringFieldsOfTypes(LocalDateTime.class)
                                       .isEqualTo(expected);
                     }
                    );
    }
}
