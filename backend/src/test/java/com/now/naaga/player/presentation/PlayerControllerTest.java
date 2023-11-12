package com.now.naaga.player.presentation;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.ControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.RankResponse;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class PlayerControllerTest extends ControllerTest {

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
        final ExtractableResponse<Response> response = given(player)
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

    // TODO: 요청 파라미터가 잘못돼었을때(o)
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
