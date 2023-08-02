package com.now.naaga.game.presentation;

import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.fixture.GameFixture.SEOUL_TO_JEJU_GAME;
import static com.now.naaga.game.fixture.MemberFixture.MEMBER_IRYE;
import static com.now.naaga.game.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.member.fixture.MemberFixture.MEMBER_EMAIL;
import static com.now.naaga.member.fixture.MemberFixture.MEMBER_PASSWORD;
import static com.now.naaga.place.fixture.PlaceFixture.JEJU_PLACE;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.CreateHintRequest;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameResultResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.game.presentation.dto.HintResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.game.repository.HintRepository;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.place.presentation.dto.CoordinateResponse;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class GameControllerTest extends CommonControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private HintRepository hintRepository;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void 게임을_포기하면_게임_결과를_업데이트_한다() throws InterruptedException {
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(IN_PROGRESS, destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표, 1, Collections.emptyList(),
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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));

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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));

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
        Player otherPlayer = playerRepository.save(new Player("Irye", new Score(100), MEMBER_IRYE()));

        Player chae = new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"));
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", chae));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));

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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(DONE, destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표, 0, Collections.emptyList(),
                LocalDateTime.now(), null));

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(game.getPlayer().getMember().getEmail(), game.getPlayer().getMember().getPassword())
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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(game.getPlayer().getMember().getEmail(), game.getPlayer().getMember().getPassword())
                .contentType(ContentType.JSON)
                .body(new FindGameByIdCommand(game.getId(), game.getPlayer().getId()))
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
        Player notSavedPlayer = PLAYER("irye", MEMBER_IRYE());
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com", "0121"))));
        Game game = new Game(Long.MAX_VALUE, IN_PROGRESS, destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표, 3, Collections.emptyList(), null, null);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(game.getPlayer().getMember().getEmail(), game.getPlayer().getMember().getPassword())
                .contentType(ContentType.JSON)
                .body(new FindGameByIdCommand(game.getId(), game.getPlayer().getId()))
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

    @Test
    public void 게임_아이디로_게임결과를_조회한다() {
        // given
        final Place place = placeRepository.save(JEJU_PLACE());
        final Game game1 = gameRepository.save(SEOUL_TO_JEJU_GAME(place));
        final GameResult gameResult1 = gameResultRepository.save(new GameResult(ResultType.SUCCESS, new Score(12), game1));
        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic("kokodak@koko.dak", "1234")
                .when()
                .get("/games/" + game1.getId() + "/result")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final GameResultResponse gameResultResponse = response.as(GameResultResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(gameResultResponse.id()).isEqualTo(gameResult1.getId());
            softly.assertThat(gameResultResponse.GameId()).isEqualTo(game1.getId());
            softly.assertThat(gameResultResponse.destination().name()).isEqualTo("JEJU");
            softly.assertThat(gameResultResponse.resultType()).isEqualTo(ResultType.SUCCESS);
        });
    }

    @Test
    public void 모든_게임_결과를_도착시간_기준으로_내림차순하여_조회한다() {
        // given
        Place place = placeRepository.save(JEJU_PLACE());
        Game game1 = gameRepository.save(SEOUL_TO_JEJU_GAME(place));
        Game game2 = gameRepository.save(SEOUL_TO_JEJU_GAME(place));
        GameResult gameResult1 = gameResultRepository.save(new GameResult(ResultType.SUCCESS, new Score(12), game1));
        GameResult gameResult2 = gameResultRepository.save(new GameResult(ResultType.FAIL, new Score(0), game2));
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic("kokodak@koko.dak", "1234")
                .param("sort-by", "time")
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

    @Test
    void 힌트를_생성한다() {
        // given & when
        final Place place = placeRepository.save(JEJU_PLACE());
        final Game game = gameRepository.save(SEOUL_TO_JEJU_GAME(place));

        final CoordinateRequest SEOUL_COORDINATE = new CoordinateRequest(37.535978, 126.981654);

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(MEMBER_EMAIL, MEMBER_PASSWORD)
                .contentType(ContentType.JSON)
                .body(new CreateHintRequest(SEOUL_COORDINATE))
                .when()
                .post("/games/{gameId}/hints", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final String location = extract.header("Location");
        final HintResponse actual = extract.as(HintResponse.class);
        final HintResponse expected = new HintResponse(
                null,
                Direction.SOUTH.name(),
                CoordinateResponse.from(SEOUL_POSITION()));

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
            softAssertions.assertThat(location).isEqualTo("/games/" + game.getId() + "/hints/" + actual.id());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        });
    }

    @Test
    void 힌트_id를_통해_힌트를_조회한다() {
        // given & when
        final Place place = placeRepository.save(JEJU_PLACE());
        final Game game = gameRepository.save(SEOUL_TO_JEJU_GAME(place));
        final Hint hint = hintRepository.save(new Hint(SEOUL_POSITION(), Direction.SOUTH, game));

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic(MEMBER_EMAIL, MEMBER_PASSWORD)
                .when()
                .get("/games/{gameId}/hints/{hintId}", game.getId(), hint.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final HintResponse actual = extract.as(HintResponse.class);
        final HintResponse expected = new HintResponse(
                hint.getId(),
                Direction.SOUTH.name(),
                CoordinateResponse.from(SEOUL_POSITION()));

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        });
    }
}
