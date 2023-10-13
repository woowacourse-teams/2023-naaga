package com.now.naaga.letter.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.*;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST_IN_PROGRESS;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LetterControllerTest extends CommonControllerTest {

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
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

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
                }
        );
    }
}
