package com.now.naaga.acceptance.game;

import com.now.naaga.acceptance.ControllerTest;
import com.now.naaga.game.domain.*;
import com.now.naaga.game.presentation.dto.GameResultResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GameControllerTest extends ControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameResultRepository gameResultRepository;

    private Long gameId_1;
    private Long gameId_2;
    private Long gameResultId_1;
    private Long gameResultId_2;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        final Member saveMember1 = memberRepository.save(new Member("chaechae@woo.com", "1234"));
        final Member saveMember2 = memberRepository.save(new Member("irea@woo.com", "1234"));
        final Member saveMember3 = memberRepository.save(new Member("cherry@woo.com", "1234"));

        final Player player1 = playerRepository.save(new Player("채채", new Score(15), saveMember1));
        final Player player2 = playerRepository.save(new Player("이레", new Score(17), saveMember2));
        final Player player3 = playerRepository.save(new Player("채리", new Score(20), saveMember3));

        final Place place1 = new Place("어딘가", "멋진곳", new Position(BigDecimal.valueOf(37.514258), BigDecimal.valueOf(127.100883)), "imageUrl", player1);
        final Place place2 = new Place("어딘가에", "더멋진곳", new Position(BigDecimal.valueOf(37.514258), BigDecimal.valueOf(127.100883)), "imageUrl", player1);
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
        final GameResult saveGameResult1 = gameResultRepository.save(gameResult1);
        final GameResult saveGameResult2 = gameResultRepository.save(gameResult2);

        gameId_1 = saveGame1.getId();
        gameResultId_1 = saveGameResult1.getId();
        gameId_2 = saveGame2.getId();
        gameResultId_2 = saveGameResult2.getId();
    }

    @Test
    public void 게임_아이디로_게임결과를_조회한다() {
        // given
        final String encodedCredentials = calculateEncodedCredentials();
        final Long gameId1 = gameId_1;

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Basic " + encodedCredentials)
                .when().get("/games/" + gameId1 + "/result")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final GameResultResponse gameResultResponse = response.as(GameResultResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(gameResultResponse.id()).isEqualTo(gameResultId_1);
            softly.assertThat(gameResultResponse.GameId()).isEqualTo(gameId1);
            softly.assertThat(gameResultResponse.destination().name()).isEqualTo("어딘가");
            softly.assertThat(gameResultResponse.resultType()).isEqualTo(ResultType.SUCCESS);
        });
    }

    @Test
    public void 모든_게임_결과를_도착시간_기준으로_내림차순하여_조회한다() {
        // given
        final String encodedCredentials = calculateEncodedCredentials();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Basic " + encodedCredentials)
                .param("sortBy", "time")
                .param("order", "descending")
                .when().get("/games/results")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<GameResultResponse> gameResultResponses = response.jsonPath().getList(".", GameResultResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(gameResultResponses).hasSize(2);
            softly.assertThat(gameResultResponses.get(0).GameId()).isEqualTo(2L);
            softly.assertThat(gameResultResponses.get(1).GameId()).isEqualTo(1L);
        });

    }

    private String calculateEncodedCredentials() {
        final String username = "chaechae@woo.com";
        final String password = "1234";
        final String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
