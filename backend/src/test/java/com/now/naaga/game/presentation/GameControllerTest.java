package com.now.naaga.game.presentation;

import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.fixture.GameFixture.GAME_IN_PROGRESS;
import static com.now.naaga.game.fixture.MemberFixture.MEMBER_IRYE;
import static com.now.naaga.game.fixture.PlaceFixture.잠실_루터회관;
import static com.now.naaga.game.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실역_교보문고_좌표;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
    private Member member;
    private Player player;
    private Place destination;
    private Position startPosition;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
        member = memberRepository.save(new Member("chae@gmail.com", "0121"));
        player = playerRepository.save(new Player("chae", new Score(1000), member));
        destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", player));
        startPosition = 잠실역_교보문고_좌표;
    }
    
    @Test
    void 게임을_포기하면_게임_결과를_업데이트_한다() throws InterruptedException {
        game = gameRepository.save(new Game(player, destination, startPosition));
        Thread.sleep(1000);
        
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic("chae@gmail.com", "0121")
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("GIVE_UP", new CoordinateRequest(37.515546, 127.102902)))// 역삼역 좌표
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
                }
        );
    }
    
    @Test
    void 게임을_도착_성공으로_종료하면_게임_결과를_업데이트_한다() throws InterruptedException {
        // given & when
        game = gameRepository.save(new Game(player, destination, startPosition));
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
    void 마지막_시도에_도착_실패하면_게임_결과를_업데이트_한다() throws InterruptedException {
        // given & when
        
        game = gameRepository.save(new Game(IN_PROGRESS, player, destination, startPosition, 1, Collections.emptyList(),
                LocalDateTime.now(), null));
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
    void 잔여_횟수가_남았지만_도착_실패하면_예외가_발생한다() throws InterruptedException {
        // given & when
        game = gameRepository.save(new Game(player, destination, startPosition));
        
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
    
    @Test
    void 사용자의_인증_정보가_존재하지_않는_경우_예외가_발생한다() {
        game = gameRepository.save(new Game(player, destination, startPosition));
        
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic("gamja@gmail.com", "1234")
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("GIVE_UP", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        
        final ExceptionResponse expected = new ExceptionResponse(200, "사용자 정보가 존재하지 않습니다.");
        
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(expected);
        });
    }
    
    @Test
    void 특정_게임에_접근_권한이_없는경우_예외가_발생한다() {
        final Member member1 = memberRepository.save(MEMBER_IRYE());
        Player otherPlayer = playerRepository.save(new Player("Irye", new Score(100), member1));
        game = gameRepository.save(new Game(player, destination, startPosition));
        
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(otherPlayer.getMember().getEmail(), otherPlayer.getMember().getPassword())
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        
        final ExceptionResponse expected = new ExceptionResponse(402, "게임에 접근할 수 있는 권한이 없습니다.");
        
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(expected);
        });
    }
    
    @Test
    void 제공된_시도횟수를_초과한_경우_또는_이미_종료된_게임인_경우_예외가_발생한다() {
        game = gameRepository.save(new Game(DONE, player, destination, startPosition, 0, Collections.emptyList(),
                LocalDateTime.now(), null));
        
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        
        final ExceptionResponse expected = new ExceptionResponse(404, "이미 종료된 게임입니다.");
        
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(expected);
        });
    }
    
    @Test
    void 게임_식별자로_게임을_조회한다() {
        // given & when
        game = gameRepository.save(GAME_IN_PROGRESS(player, destination, startPosition));
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .contentType(ContentType.JSON)
                .body(new FindGameByIdCommand(game.getId(), player.getId()))
                .when()
                .get("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final GameResponse actual = extract.as(GameResponse.class);
        
        final GameResponse expected = new GameResponse(null, PlaceResponse.from(destination),
                IN_PROGRESS.toString());
        
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
    void 게임_식별자로_조회하려는_게임이_존재하지_않는_경우_예외가_발생한다() {
        // given & when
        Player notSavedPlayer = PLAYER("irye",MEMBER_IRYE());
        game = new Game(Long.MAX_VALUE,IN_PROGRESS, notSavedPlayer, 잠실_루터회관(notSavedPlayer), startPosition, 3, Collections.emptyList(), null, null);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .contentType(ContentType.JSON)
                .body(new FindGameByIdCommand(game.getId(), player.getId()))
                .when()
                .get("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        
        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        
        final ExceptionResponse expected = new ExceptionResponse(401, "게임이 존재하지 않습니다.");
        
        assertSoftly(softAssertions -> {
                    softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
                    softAssertions.assertThat(actual)
                            .usingRecursiveComparison()
                            .ignoringExpectedNullFields()
                            .ignoringFieldsOfTypes(LocalDateTime.class)
                            .isEqualTo(expected);
                }
        );
    }
    
}
