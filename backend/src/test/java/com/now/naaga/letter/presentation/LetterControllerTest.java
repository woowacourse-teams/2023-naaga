package com.now.naaga.letter.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.letter.presentation.dto.NearByLetterResponse;
import com.now.naaga.letter.repository.LetterRepository;
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
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.now.naaga.common.fixture.PositionFixture.*;
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
    private LetterService letterService;

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

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

        final AuthToken generate = authTokenGenerator.generate(destination.getRegisteredPlayer().getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
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
