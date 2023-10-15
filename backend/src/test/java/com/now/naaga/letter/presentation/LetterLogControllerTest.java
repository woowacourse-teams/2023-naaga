package com.now.naaga.letter.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.*;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static com.now.naaga.common.fixture.PositionFixture.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LetterLogControllerTest extends CommonControllerTest {

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private WriteLetterLogRepository writeLetterLogRepository;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    @Autowired
    private ReadLetterLogBuilder readLetterLogBuilder;

    @Autowired
    private WriteLetterLogBuilder writeLetterLogBuilder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    // 잘못된 파라미터는 예외를 던진다.
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

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
}
