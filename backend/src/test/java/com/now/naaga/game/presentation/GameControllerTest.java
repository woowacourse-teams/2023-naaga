package com.now.naaga.game.presentation;

import static com.now.naaga.game.fixture.GameTestFixture.MEMBER2;
import static com.now.naaga.game.fixture.GameTestFixture.잠실_루터회관;
import static com.now.naaga.game.fixture.GameTestFixture.잠실역_교보문고_좌표;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

class GameControllerTest extends CommonControllerTest {
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    private Game game;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        final Member member = memberRepository.save(MEMBER2);
        final Player player = playerRepository.save(new Player("chae", new Score(1000), member));
        final Place destination = placeRepository.save(잠실_루터회관);
        final Position startPosition = 잠실역_교보문고_좌표;
        game = gameRepository.save(new Game(player, destination, startPosition));
    }
    
    @Test
    void 게임을_도착_성공으로_종료하면_게임_결과를_업데이트_한다() throws InterruptedException {
        // given & when
        Thread.sleep(1000);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic("chae@gmail.com", "0121")
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final GameStatusResponse actual = extract.as(GameStatusResponse.class);
        
        final GameStatusResponse expected = new GameStatusResponse(null, "DONE");
        
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(expected);
        });
    }
    
    @Test
    void 잔여_횟수가_남았지만_도착_실패하면_예외가_발생_한다() throws InterruptedException {
        // given & when
        Thread.sleep(1000);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic("chae@gmail.com", "0121")
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.500845, 127.036953)))// 역삼역 좌표
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        
        final ExceptionResponse expected = new ExceptionResponse(403, "목적지에 도착하지 않았습니다.");
        
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(expected);
        });
    }
}
