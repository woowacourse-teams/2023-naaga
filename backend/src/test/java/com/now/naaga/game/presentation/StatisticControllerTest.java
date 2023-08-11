package com.now.naaga.game.presentation;

import com.now.naaga.auth.domain.AuthTokens;
import com.now.naaga.auth.infrastructure.jwt.JwtGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.domain.*;
import com.now.naaga.game.presentation.dto.StatisticResponse;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
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
    GameService gameService;
    
    @Autowired
    JwtGenerator jwtGenerator;
    
    @Autowired
    PlaceRepository placeRepository;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }
    
    
    @Test
    void 맴버의_통계를_조회한다() throws InterruptedException {
        //given
        final Member member1 = new Member("chaechae@woo.com");
        final Player player1 = new Player("채채", new Score(15), member1);
        final Position 잠실_교보문고_좌표 = new Position(BigDecimal.valueOf(37.514258), BigDecimal.valueOf(127.100883));
        final Place 잠실_교보문고 = new Place("잠실_교보문고", "잠실교보문고",
                잠실_교보문고_좌표, "imageUrl", player1);
        placeRepository.save(잠실_교보문고);
        
        final Position 게임_시작_위치_잠실_루터회관 = new Position(BigDecimal.valueOf(37.515302), BigDecimal.valueOf(127.102832));
        final Position 게임_종료_위치_잠실_교보문고_근처 = new Position(BigDecimal.valueOf(37.514318), BigDecimal.valueOf(127.100907));
    
        final Game saveGame1 = gameService.createGame(
                new CreateGameCommand(player1.getId(), 게임_시작_위치_잠실_루터회관));
        Thread.sleep(1000);
        gameService.endGame(
                new EndGameCommand(player1.getId(), EndType.ARRIVED, 게임_종료_위치_잠실_교보문고_근처, saveGame1.getId()));
        
        final Game saveGame2 = gameService.createGame(
                new CreateGameCommand(player1.getId(), 게임_시작_위치_잠실_루터회관));
        Thread.sleep(1000);
        gameService.endGame(
                new EndGameCommand(player1.getId(), EndType.GIVE_UP, 게임_종료_위치_잠실_교보문고_근처, saveGame2.getId()));
        
        final Statistic statistic = gameService.findStatistic(new PlayerRequest(player1.getId()));
        
        final Long memberId = player1.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();
        
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
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
