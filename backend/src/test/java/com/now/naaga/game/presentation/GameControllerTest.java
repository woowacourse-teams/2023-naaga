package com.now.naaga.game.presentation;

import static com.now.naaga.auth.exception.AuthExceptionType.NOT_EXIST_HEADER;
import static com.now.naaga.common.fixture.PositionFixture.서울_좌표;
import static com.now.naaga.common.fixture.PositionFixture.역삼역_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_근처_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.common.fixture.PositionFixture.제주_좌표;
import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.domain.ResultType.FAIL;
import static com.now.naaga.game.domain.ResultType.SUCCESS;
import static com.now.naaga.game.exception.GameExceptionType.ALREADY_DONE;
import static com.now.naaga.game.exception.GameExceptionType.ALREADY_IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.CAN_NOT_FIND_PLACE;
import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;
import static com.now.naaga.game.exception.GameExceptionType.NOT_ARRIVED;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.auth.domain.AuthTokens;
import com.now.naaga.auth.infrastructure.jwt.JwtGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.GameResultBuilder;
import com.now.naaga.common.builder.MemberBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameResultResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.game.presentation.dto.HintResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.HintRepository;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.presentation.dto.CoordinateResponse;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerResponse;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private JwtGenerator jwtGenerator;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private HintRepository hintRepository;

    @Autowired
    private GameResultBuilder gameResultBuilder;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private MemberBuilder memberBuilder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void 게임_생성_요청시_진행중인_게임이_없으면서_주변에_추천_장소가_있다면_게임을_정상적으로_생성한다() {
        // given
        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        CoordinateRequest coordinateRequest = new CoordinateRequest(잠실역_교보문고_좌표.getLatitude().doubleValue(),
                                                                    잠실역_교보문고_좌표.getLongitude().doubleValue());

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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .registeredPlayer(player)
                                              .build();

        gameBuilder.init()
                   .place(destination)
                   .player(player)
                   .startPosition(잠실역_교보문고_좌표)
                   .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        CoordinateRequest coordinateRequest = new CoordinateRequest(잠실역_교보문고_좌표.getLatitude().doubleValue(),
                                                                    잠실역_교보문고_좌표.getLongitude().doubleValue());

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
        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        CoordinateRequest coordinateRequest = new CoordinateRequest(역삼역_좌표.getLatitude().doubleValue(),
                                                                    역삼역_좌표.getLongitude().doubleValue());

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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .registeredPlayer(player)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();
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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .registeredPlayer(player)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();
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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .registeredPlayer(player)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .remainingAttempts(1)
                                     .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();
        Thread.sleep(1000);

        final ExtractableResponse<Response> extract = RestAssured.given().log().all()
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
        final GameStatusResponse expected = GameStatusResponse.from(gameRepository.findById(game.getId()).get());

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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .registeredPlayer(player)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .registeredPlayer(player)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();

        final Long memberId = destination.getRegisteredPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

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
        final Member member1 = memberBuilder.init()
                                            .email("member1@email.com")
                                            .build();

        final Member member2 = memberBuilder.init()
                                            .email("member2@email.com")
                                            .build();

        final Player player1 = playerBuilder.init()
                                            .nickname("이레")
                                            .totalScore(new Score(100))
                                            .member(member1)
                                            .build();

        final Player player2 = playerBuilder.init()
                                            .nickname("채채")
                                            .totalScore(new Score(1000))
                                            .member(member2)
                                            .build();

        final Place place = placeBuilder.init()
                                        .position(잠실_루터회관_정문_좌표)
                                        .registeredPlayer(player1)
                                        .build();

        final Game game = gameBuilder.init()
                                     .player(player1)
                                     .place(place)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();

        final Long memberId = player2.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        final CoordinateRequest coordinateRequest = new CoordinateRequest(잠실_루터회관_정문_근처_좌표.getLatitude().doubleValue(),
                                                                          잠실_루터회관_정문_근처_좌표.getLongitude().doubleValue());

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(new EndGameRequest("ARRIVED", coordinateRequest))
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
        final Player player = playerBuilder.init()
                                           .build();

        final Game game = gameBuilder.init()
                                     .gameStatus(DONE)
                                     .player(player)
                                     .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
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
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
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
        final GameResponse actual = extract.as(GameResponse.class);
        final GameResponse expected = GameResponse.from(game);

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
        final Player player = playerBuilder.init()
                                           .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/games/{gameId}", 1L)
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
        final Player player = playerBuilder.init()
                                           .build();

        final Game game = gameBuilder.init()
                                     .player(player)
                                     .endTime(LocalDateTime.now().plusHours(1))
                                     .build();

        final GameResult gameResult = gameResultBuilder.init()
                                                       .resultType(SUCCESS)
                                                       .game(game)
                                                       .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                                                                  .log().all()
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .header("Authorization", "Bearer " + accessToken)
                                                                  .when()
                                                                  .get("/games/{gameId}/result", game.getId())
                                                                  .then().log().all()
                                                                  .statusCode(HttpStatus.OK.value())
                                                                  .extract();

        // then
        final GameResultResponse actual = response.as(GameResultResponse.class);
        final GameResultResponse expected = GameResultResponse.from(
                GameRecord.from(gameResult));

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                  .usingRecursiveComparison()
                  .isEqualTo(expected);
        });
    }

    @Test
    public void 모든_게임_결과를_도착시간_기준으로_내림차순하여_조회한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Game game1 = gameBuilder.init()
                                      .gameStatus(DONE)
                                      .player(player)
                                      .endTime(LocalDateTime.now().plusHours(1))
                                      .build();

        final Game game2 = gameBuilder.init()
                                      .gameStatus(DONE)
                                      .player(player)
                                      .endTime(LocalDateTime.now().plusHours(10))
                                      .build();

        final GameResult gameResult1 = gameResultBuilder.init()
                                                        .resultType(FAIL)
                                                        .game(game1)
                                                        .build();

        final GameResult gameResult2 = gameResultBuilder.init()
                                                        .resultType(SUCCESS)
                                                        .game(game2)
                                                        .build();

        final Long memberId = player.getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                  .header("Authorization", "Bearer " + accessToken)
                                                                  .param("sort-by", "time")
                                                                  .param("order", "descending")
                                                                  .when()
                                                                  .get("/games/results")
                                                                  .then().log().all()
                                                                  .statusCode(HttpStatus.OK.value())
                                                                  .extract();

        // then
        final List<GameResultResponse> actual = response.as(new TypeRef<>() {
        });
        final List<GameResultResponse> expected = List.of(GameResultResponse.from(GameRecord.from(gameResult2)),
                                                          GameResultResponse.from(GameRecord.from(gameResult1)));

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                  .hasSize(2)
                  .usingRecursiveComparison()
                  .isEqualTo(expected);
        });
    }

    @Test
    void 힌트를_생성한다() {
        // given & when
        final Place place = placeBuilder.init()
                                        .position(제주_좌표)
                                        .build();

        final Game game = gameBuilder.init()
                                     .place(place)
                                     .startPosition(서울_좌표)
                                     .build();

        final Long memberId = game.getPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        final CoordinateRequest SEOUL_COORDINATE = new CoordinateRequest(서울_좌표.getLatitude().doubleValue(),
                                                                         서울_좌표.getLongitude().doubleValue());

        final ExtractableResponse<Response> extract = RestAssured.given().log().all()
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
                CoordinateResponse.from(서울_좌표));

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
        final Place place = placeBuilder.init()
                                        .position(제주_좌표)
                                        .build();

        final Game game = gameBuilder.init()
                                     .place(place)
                                     .startPosition(서울_좌표)
                                     .build();

        final Hint hint = hintRepository.save(new Hint(서울_좌표, Direction.SOUTH, game));

        final Long memberId = game.getPlayer().getMember().getId();
        final AuthTokens generate = jwtGenerator.generate(memberId);
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
                CoordinateResponse.from(서울_좌표));

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(actual)
                          .usingRecursiveComparison()
                          .isEqualTo(expected);
        });
    }
}
