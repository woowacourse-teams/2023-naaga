package com.now.naaga.game.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.domain.*;
import com.now.naaga.game.presentation.dto.*;
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
import com.now.naaga.player.presentation.dto.PlayerResponse;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static com.now.naaga.auth.exception.AuthExceptionType.NOT_EXIST_HEADER;
import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.*;
import static com.now.naaga.game.fixture.GameFixture.SEOUL_TO_JEJU_GAME;
import static com.now.naaga.game.fixture.MemberFixture.MEMBER_IRYE;
import static com.now.naaga.game.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.place.fixture.PlaceFixture.JEJU_PLACE;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class GameControllerTest extends CommonControllerTest {

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

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
    void 게임_생성_요청시_진행중인_게임이_없으면서_주변에_추천_장소가_있다면_게임을_정상적으로_생성한다() {
        // given
        final Place destination = placeRepository.save(
                new Place(
                        "잠실루터회관",
                        "잠실루터회관이다.",
                        잠실_루터회관_정문_좌표,
                        "잠실루터회관IMAGE",
                        new Player("kokodak",
                                new Score(1000),
                                new Member("koko@da.k")
                        )));


        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        CoordinateRequest coordinateRequest = new CoordinateRequest(37.514258, 127.100883);

        // when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(coordinateRequest)
                .when()
                .post("/games")
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final String location = extract.header("Location");
        final Long gameId = getIdFromLocationHeader(extract);
        final GameResponse actual = extract.as(GameResponse.class);
        final GameResponse expected = new GameResponse(
                gameId,
                null,
                IN_PROGRESS.name(),
                MAX_ATTEMPT_COUNT,
                com.now.naaga.game.presentation.dto.CoordinateResponse.of(잠실역_교보문고_좌표),
                PlaceResponse.from(destination),
                PlayerResponse.from(destination.getRegisteredPlayer()),
                new ArrayList<>());

        assertSoftly(softAssertions -> {
                    softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
                    softAssertions.assertThat(location).isEqualTo("/games/" + gameId);
                    softAssertions.assertThat(actual)
                            .usingRecursiveComparison()
                            .ignoringExpectedNullFields()
                            .ignoringFieldsOfTypes(LocalDateTime.class)
                            .isEqualTo(expected);
                }
        );
    }

    @Test
    void 게임_생성_요청시_진행중인_게임이_있다면_예외가_발생한다() {
        // given
        final Place destination = placeRepository.save(
                new Place(
                        "잠실루터회관",
                        "잠실루터회관이다.",
                        잠실_루터회관_정문_좌표,
                        "잠실루터회관IMAGE",
                        new Player("kokodak",
                                new Score(1000),
                                new Member("koko@da.k")
                        )));
        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
        CoordinateRequest coordinateRequest = new CoordinateRequest(37.514258, 127.100883);

        // when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(coordinateRequest)
                .when()
                .post("/games")
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(
                ALREADY_IN_PROGRESS.errorCode(),
                ALREADY_IN_PROGRESS.errorMessage());

        assertSoftly(softAssertions -> {
                    softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    softAssertions.assertThat(actual)
                            .usingRecursiveComparison()
                            .isEqualTo(expected);
                }
        );
    }

    @Test
    void 게임_생성_요청시_주변에_추천_장소가_없다면_예외가_발생한다() {
        // given
        final Place destination = placeRepository.save(
                new Place(
                        "잠실루터회관",
                        "잠실루터회관이다.",
                        잠실_루터회관_정문_좌표,
                        "잠실루터회관IMAGE",
                        new Player("kokodak",
                                new Score(1000),
                                new Member("koko@da.k")
                        )));
        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        CoordinateRequest coordinateRequest = new CoordinateRequest(37.500845, 127.036953);

        // when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(coordinateRequest)
                .when()
                .post("/games")
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(CAN_NOT_FIND_PLACE.errorCode(), CAN_NOT_FIND_PLACE.errorMessage());

        assertSoftly(softAssertions -> {
                    softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    softAssertions.assertThat(actual)
                            .usingRecursiveComparison()
                            .isEqualTo(expected);
                }
        );
    }

    @Test
    void 게임을_포기하면_게임_결과를_업데이트_한다() throws InterruptedException {
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
        Thread.sleep(1000);

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
        Thread.sleep(1000);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        Game game = gameRepository.save(new Game(IN_PROGRESS, destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표, 1, Collections.emptyList(),
                LocalDateTime.now(), null));
        Thread.sleep(1000);

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
    void 잔여_횟수가_남았지만_도착_실패하면_예외가_발생하고_시도횟수를_1차감한다() throws InterruptedException {
        // given & when
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
        final int beforeRemainingAttempts = game.getRemainingAttempts();
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.500845, 127.036953)))// 역삼역 좌표
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();
        final ExtractableResponse<Response> extractAfter = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(NOT_ARRIVED.errorCode(), NOT_ARRIVED.errorMessage());
        final GameResponse endGame = extractAfter.body().as(GameResponse.class);
        final int afterRemainingAttempts = endGame.remainingAttempts();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFieldsOfTypes(LocalDateTime.class)
                    .isEqualTo(expected);
            softAssertions.assertThat(afterRemainingAttempts).isEqualTo(beforeRemainingAttempts - 1);
        });
    }

    @Test
    void 사용자의_인증_정보가_존재하지_않는_경우_예외가_발생한다() {
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("GIVE_UP", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);

        final ExceptionResponse expected = new ExceptionResponse(NOT_EXIST_HEADER.errorCode(), NOT_EXIST_HEADER.errorMessage());

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

        Player chae = new Player("chae", new Score(1000), new Member("chae@gmail.com"));
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", chae));
        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));

        final Long memberId = otherPlayer.getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);

        final ExceptionResponse expected = new ExceptionResponse(INACCESSIBLE_AUTHENTICATION.errorCode(), INACCESSIBLE_AUTHENTICATION.errorMessage());

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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));
        Game game = gameRepository.save(new Game(DONE, destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표, 0, Collections.emptyList(),
                LocalDateTime.now(), null));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", new CoordinateRequest(37.515546, 127.102902)))
                .when()
                .patch("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);

        final ExceptionResponse expected = new ExceptionResponse(ALREADY_DONE.errorCode(), ALREADY_DONE.errorMessage());

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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        Game game = gameRepository.save(new Game(destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표));
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new FindGameByIdCommand(game.getId(), game.getPlayer().getId()))
                .when()
                .get("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final GameResponse actual = extract.as(GameResponse.class);
        final GameResponse expected = new GameResponse(
                game.getId(),
                null,
                IN_PROGRESS.name(),
                MAX_ATTEMPT_COUNT,
                com.now.naaga.game.presentation.dto.CoordinateResponse.of(잠실역_교보문고_좌표),
                PlaceResponse.from(destination),
                PlayerResponse.from(destination.getRegisteredPlayer()),
                new ArrayList<>());

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
        Place destination = placeRepository.save(new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE", new Player("chae", new Score(1000), new Member("chae@gmail.com"))));
        Game game = new Game(Long.MAX_VALUE, IN_PROGRESS, destination.getRegisteredPlayer(), destination, 잠실역_교보문고_좌표, 3, Collections.emptyList(), null, null);

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new FindGameByIdCommand(game.getId(), game.getPlayer().getId()))
                .when()
                .get("/games/{gameId}", game.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);

        final ExceptionResponse expected = new ExceptionResponse(NOT_EXIST.errorCode(), NOT_EXIST.errorMessage());

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

        final Long memberId = game1.getPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
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
            softly.assertThat(gameResultResponse.gameId()).isEqualTo(game1.getId());
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

        final Long memberId = game1.getPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
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
            softly.assertThat(gameResultResponses.get(0).gameId()).isEqualTo(2L);
            softly.assertThat(gameResultResponses.get(1).gameId()).isEqualTo(1L);
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
        final Long memberId = game.getPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final CoordinateRequest SEOUL_COORDINATE = new CoordinateRequest(37.535978, 126.981654);

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(SEOUL_COORDINATE)
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

        final Long memberId = game.getPlayer().getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId, 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
