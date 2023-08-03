package com.now.naaga.game.presentation;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.game.domain.*;
import com.now.naaga.game.presentation.dto.StatisticResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StatisticControllerTest extends CommonControllerTest {

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameResultRepository gameResultRepository;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void 맴버의_통계를_조회한다() {
        //given
        final Member member1 = new Member("chaechae@woo.com", "1234");
        final Member member2 = new Member("chaechae2@woo.com", "1234");

        final Player player1 = new Player("채채", new Score(15), member1);
        final Player player2 = new Player("채채2", new Score(12), member2);

        final Place place1 = new Place("어딘가", "멋진곳", new Position(BigDecimal.valueOf(37.514258), BigDecimal.valueOf(127.100883)), "imageUrl", player1);
        final Place place2 = new Place("어딘가에", "더멋진곳", new Position(BigDecimal.valueOf(37.514258), BigDecimal.valueOf(127.100883)), "imageUrl", player2);
        final Place savePlace1 = placeRepository.save(place1);
        final Place savePlace2 = placeRepository.save(place2);
        List<Hint> hints = new ArrayList<>();
        final LocalDateTime startTime1 = LocalDateTime.of(2023, 8, 2, 13, 30);
        final LocalDateTime endTime1 = LocalDateTime.of(2023, 8, 2, 15, 30);

        final LocalDateTime startTime2 = LocalDateTime.of(2024, 8, 2, 13, 30);
        final LocalDateTime endTime2 = LocalDateTime.of(2024, 8, 2, 15, 30);

        final Game game1 = new Game(GameStatus.DONE, player1, savePlace1,
                new Position(BigDecimal.valueOf(37.512256),
                        BigDecimal.valueOf(127.112584)), 2, hints, startTime1, endTime1);
        final Game game2 = new Game(GameStatus.DONE, player1, savePlace2,
                new Position(BigDecimal.valueOf(37.512256),
                        BigDecimal.valueOf(127.112584)), 3, hints, startTime2, endTime2);
        final Game saveGame1 = gameRepository.save(game1);
        final Game saveGame2 = gameRepository.save(game2);
        final GameResult gameResult1 = new GameResult(ResultType.SUCCESS, new Score(15), saveGame1);
        final GameResult gameResult2 = new GameResult(ResultType.FAIL, new Score(10), saveGame2);
        gameResultRepository.save(gameResult1);
        gameResultRepository.save(gameResult2);
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic("chaechae@woo.com", "1234")
                .when().get("/statistics/my")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final StatisticResponse statisticResponse = response.as(StatisticResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(statisticResponse.gameCount()).isEqualTo(2);
            softly.assertThat(statisticResponse.successGameCount()).isEqualTo(1);
            softly.assertThat(statisticResponse.failGameCount()).isEqualTo(1);
            softly.assertThat(statisticResponse.totalDistance()).isEqualTo(2);
            softly.assertThat(statisticResponse.totalPlayTime()).isNotNull();
            softly.assertThat(statisticResponse.totalUsedHintCount()).isEqualTo(0);
        });
    }
}
