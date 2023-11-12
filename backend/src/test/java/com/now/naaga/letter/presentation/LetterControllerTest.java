package com.now.naaga.letter.presentation;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_BODY;
import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static com.now.naaga.common.fixture.PositionFixture.GS25_방이도곡점_좌표;
import static com.now.naaga.common.fixture.PositionFixture.역삼역_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_110미터_앞_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST_IN_PROGRESS;
import static com.now.naaga.player.exception.PlayerExceptionType.PLAYER_NOT_FOUND;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.ControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.presentation.dto.CoordinateResponse;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.presentation.dto.LetterRequest;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.letter.presentation.dto.NearByLetterResponse;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
class LetterControllerTest extends ControllerTest {

    @Test
    void 주변_쪽지를_모두_조회한다() {
        // given
        final Place destination = placeBuilder.init()
                                              .build();
        final Player player = playerBuilder.init()
                                           .build();
        final Letter letter1 = letterBuilder.init()
                                            .build();
        final Letter letter2 = letterBuilder.init()
                                            .position(잠실역_교보문고_110미터_앞_좌표)
                                            .build();

        // when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("latitude", 잠실역_교보문고_좌표.getLatitude())
                .param("longitude", 잠실역_교보문고_좌표.getLongitude())
                .when()
                .get("/letters/nearby")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<NearByLetterResponse> actual = extract.as(new TypeRef<>() {
        });
        final int statusCode = extract.statusCode();
        final List<NearByLetterResponse> expected = List.of(NearByLetterResponse.from(letter1));

        assertSoftly(softly -> {
            softly.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                  .hasSize(1)
                  .usingRecursiveComparison()
                  .isEqualTo(expected);
        });
    }

    @Test
    void 쪽지_식별자로_쪽지를_조회한다() {
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

        final Letter letter = letterBuilder.init()
                                           .position(잠실_루터회관_정문_좌표)
                                           .build();

        final ExtractableResponse<Response> extract = given(player)
                .when()
                .get("/letters/{letterId}", letter.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final LetterResponse actual = extract.as(LetterResponse.class);
        final LetterResponse expected = LetterResponse.from(letter);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(actual)
                          .usingRecursiveComparison()
                          .ignoringExpectedNullFields()
                          .ignoringFields("registerDate")
                          .isEqualTo(expected);
        });
    }

    @Test
    void 쪽지_식별자로_쪽지를_조회시_잔행중_게임이_없으면_예외가_발생한다() {
        // given & when
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Letter letter = letterBuilder.init()
                                           .position(잠실_루터회관_정문_좌표)
                                           .build();

        final ExtractableResponse<Response> extract = given(player)
                .when()
                .get("/letters/{letterId}", letter.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);

        final ExceptionResponse expected = new ExceptionResponse(NOT_EXIST_IN_PROGRESS.errorCode(), NOT_EXIST_IN_PROGRESS.errorMessage());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
            softAssertions.assertThat(actual)
                          .usingRecursiveComparison()
                          .ignoringExpectedNullFields()
                          .ignoringFieldsOfTypes(LocalDateTime.class)
                          .isEqualTo(expected);
        });
    }

    @Test
    void 쪽지_등록_요청시_쪽지를_등록한_플레이어가_존재하고_그_플레이어가_진행중인_게임이_있으면_정상적으로_쪽지를_생성한다() {
        //given
        final Player player = playerBuilder.init()
                                           .build();
        final Game game = gameBuilder.init()
                                     .player(player)
                                     .build();

        String message = "날씨가 선선하네요";
        final LetterRequest letterRequest = new LetterRequest(message,
                                                              잠실_루터회관_정문_좌표.getLatitude().doubleValue(),
                                                              잠실_루터회관_정문_좌표.getLongitude().doubleValue());

        //when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(ContentType.JSON)
                .body(letterRequest)
                .when()
                .post("/letters")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final String location = extract.header("Location");
        final Long letterId = getIdFromLocationHeader(extract);
        final LetterResponse actual = extract.as(LetterResponse.class);
        final LetterResponse expected = new LetterResponse(
                letterId,
                PlayerResponse.from(player),
                CoordinateResponse.of(잠실_루터회관_정문_좌표),
                message,
                null
        );

        assertSoftly(softAssertions -> {
                         softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
                         softAssertions.assertThat(location).isNotEqualTo("/letters" + letterId);
                         softAssertions.assertThat(actual)
                                       .usingRecursiveComparison()
                                       .ignoringExpectedNullFields()
                                       .isEqualTo(expected);
                     }
                    );
    }

    @Test
    void 쪽지_등록_요청시_쪽지를_등록한_플레이어가_존재하지_않으면_예외가_발생한다() {
        //given
        String message = "날씨가 선선하네요";
        final LetterRequest letterRequest = new LetterRequest(message,
                                                              잠실_루터회관_정문_좌표.getLatitude().doubleValue(),
                                                              잠실_루터회관_정문_좌표.getLongitude().doubleValue());

        //when
        final ExtractableResponse<Response> extract = given(new Member(1L, "email", false))
                .contentType(ContentType.JSON)
                .body(letterRequest)
                .when()
                .post("/letters")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(
                PLAYER_NOT_FOUND.errorCode(),
                PLAYER_NOT_FOUND.errorMessage()
        );

        assertSoftly(softAssertions -> {
                         softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
                         softAssertions.assertThat(actual)
                                       .usingRecursiveComparison()
                                       .isEqualTo(expected);
                     }
                    );
    }

    @Test
    void 쪽지_등록_요청시_쪽지를_등록한_진행중인_게임이_존재하지_않으면_예외가_발생한다() {
        //given
        final Player player = playerBuilder.init()
                                           .build();
        final Game finishedGame = gameBuilder.init()
                                             .player(player)
                                             .gameStatus(DONE)
                                             .build();

        String message = "날씨가 선선하네요";
        final LetterRequest letterRequest = new LetterRequest(message,
                                                              잠실_루터회관_정문_좌표.getLatitude().doubleValue(),
                                                              잠실_루터회관_정문_좌표.getLongitude().doubleValue());

        //when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(ContentType.JSON)
                .body(letterRequest)
                .when()
                .post("/letters")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(
                NOT_EXIST_IN_PROGRESS.errorCode(),
                NOT_EXIST_IN_PROGRESS.errorMessage()
        );

        assertSoftly(softAssertions -> {
                         softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
                         softAssertions.assertThat(actual)
                                       .usingRecursiveComparison()
                                       .isEqualTo(expected);
                     }
                    );
    }

    @Test
    void 게임아이디와_회원으로_읽은쪽지로그를_모두_조회한다() {
        // given & when
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Game game1 = gameBuilder.init()
                                      .place(destination)
                                      .player(player)
                                      .startPosition(잠실_루터회관_정문_좌표)
                                      .build();

        final Game game2 = gameBuilder.init()
                                      .place(destination)
                                      .player(player)
                                      .startPosition(역삼역_좌표)
                                      .build();

        final Letter letter = letterBuilder.init()
                                           .position(잠실_루터회관_정문_좌표)
                                           .build();

        final ReadLetterLog readLetterLog1 = readLetterLogBuilder.init()
                                                                 .game(game1)
                                                                 .letter(letter)
                                                                 .build();

        final ReadLetterLog readLetterLog2 = readLetterLogBuilder.init()
                                                                 .game(game2)
                                                                 .letter(letter)
                                                                 .build();

        final ExtractableResponse<Response> extract = given(player)
                .param("gameId", game1.getId())
                .param("logType", "READ")
                .when()
                .get("/letterlogs")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<LetterResponse> actual = extract.as(new TypeRef<>() {
        });
        final int statusCode = extract.statusCode();
        final List<LetterResponse> expected = List.of(LetterResponse.from(letter));

        assertSoftly(softly -> {
            softly.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                  .hasSize(1)
                  .usingRecursiveComparison()
                  .ignoringFields("registerDate")
                  .isEqualTo(expected);
        });
    }

    @Test
    void
    게임아이디와_회원으로_작성한_쪽지로그를_모두_조회한다() {
        // given & when
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실_루터회관_정문_좌표)
                                     .build();

        final Letter letter = letterBuilder.init()
                                           .position(잠실_루터회관_정문_좌표)
                                           .build();

        final WriteLetterLog writeLetterLog1 = writeLetterLogBuilder.init()
                                                                    .game(game)
                                                                    .letter(letter)
                                                                    .build();

        final ExtractableResponse<Response> extract = given(player)
                .param("gameId", game.getId())
                .param("logType", "WRITE")
                .when()
                .get("/letterlogs")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<LetterResponse> actual = extract.as(new TypeRef<>() {
        });
        final int statusCode = extract.statusCode();
        final List<LetterResponse> expected = List.of(LetterResponse.from(letter));

        assertSoftly(softly -> {
            softly.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                  .hasSize(1)
                  .usingRecursiveComparison()
                  .ignoringFields("registerDate")
                  .isEqualTo(expected);
        });
    }

    @Test
    void 쪽지로그_조회시_잘못된_파라미터는_예외를_던진다() {
        // given & when
        final Player player = playerBuilder.init()
                                           .build();

        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();

        final Game game1 = gameBuilder.init()
                                      .place(destination)
                                      .player(player)
                                      .startPosition(잠실_루터회관_정문_좌표)
                                      .build();

        final Game game2 = gameBuilder.init()
                                      .place(destination)
                                      .player(player)
                                      .startPosition(역삼역_좌표)
                                      .build();

        final Letter letter = letterBuilder.init()
                                           .position(잠실_루터회관_정문_좌표)
                                           .build();

        final Letter letter2 = letterBuilder.init()
                                            .position(GS25_방이도곡점_좌표)
                                            .build();

        final WriteLetterLog writeLetterLog1 = writeLetterLogBuilder.init()
                                                                    .game(game1)
                                                                    .letter(letter)
                                                                    .build();

        final WriteLetterLog writeLetterLog2 = writeLetterLogBuilder.init()
                                                                    .game(game2)
                                                                    .letter(letter2)
                                                                    .build();

        final ExtractableResponse<Response> extract = given(player)
                .param("gameId", game1.getId())
                .param("logType", "잘못된enum")
                .when()
                .get("/letterlogs")
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(INVALID_REQUEST_PARAMETERS.errorCode(), INVALID_REQUEST_PARAMETERS.errorMessage());

        assertSoftly(softAssertions -> {
                         softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
                         softAssertions.assertThat(actual)
                                       .usingRecursiveComparison()
                                       .ignoringExpectedNullFields()
                                       .ignoringFieldsOfTypes(LocalDateTime.class)
                                       .ignoringFields("registerDate")
                                       .isEqualTo(expected);
                     }
                    );
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void 쪽지_등록_요청시_쪽지의_내용이_blank이면_예외가_발생한다(String message) {
        //given
        final Player player = playerBuilder.init()
                                           .build();
        final Game game = gameBuilder.init()
                                     .player(player)
                                     .build();

        final LetterRequest letterRequest = new LetterRequest(message,
                                                              잠실_루터회관_정문_좌표.getLatitude().doubleValue(),
                                                              잠실_루터회관_정문_좌표.getLongitude().doubleValue());

        //when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(ContentType.JSON)
                .body(letterRequest)
                .when()
                .post("/letters")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(
                INVALID_REQUEST_BODY.errorCode(),
                INVALID_REQUEST_BODY.errorMessage()
        );

        assertSoftly(softAssertions -> {
                         softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
                         softAssertions.assertThat(actual)
                                       .usingRecursiveComparison()
                                       .isEqualTo(expected);
                     }
                    );
    }
}
