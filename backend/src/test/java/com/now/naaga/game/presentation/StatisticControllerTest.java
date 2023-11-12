package com.now.naaga.game.presentation;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.ControllerTest;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.Statistic;
import com.now.naaga.game.presentation.dto.StatisticResponse;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class StatisticControllerTest extends ControllerTest {

    @Autowired
    private GameService gameService;

    @Test
    void 맴버의_통계를_조회한다() {
        //given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .position(잠실역_교보문고_좌표)
                                        .registeredPlayer(player)
                                        .build();

        final Game game1 = gameBuilder.init()
                                      .startPosition(잠실_루터회관_정문_좌표)
                                      .player(player)
                                      .place(place)
                                      .build();

        final Game game2 = gameBuilder.init()
                                      .startPosition(잠실_루터회관_정문_좌표)
                                      .player(player)
                                      .place(place)
                                      .build();

        final EndGameCommand endGameCommand1 = new EndGameCommand(player.getId(),
                                                                  EndType.ARRIVED,
                                                                  잠실역_교보문고_좌표,
                                                                  game1.getId());

        final EndGameCommand endGameCommand2 = new EndGameCommand(player.getId(),
                                                                  EndType.GIVE_UP,
                                                                  잠실역_교보문고_좌표,
                                                                  game2.getId());

        gameService.endGame(endGameCommand1);

        gameService.endGame(endGameCommand2);

        final Statistic statistic = gameService.findStatistic(new PlayerRequest(player.getId()));

        // when
        final ExtractableResponse<Response> response = given(player)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/statistics/my")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final StatisticResponse actual = response.as(StatisticResponse.class);
        final StatisticResponse expected = StatisticResponse.from(statistic);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                  .usingRecursiveComparison()
                  .isEqualTo(expected);
        });
    }
}
