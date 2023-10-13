package com.now.naaga.letter.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.domain.Letter;
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
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static com.now.naaga.common.fixture.PositionFixture.역삼역_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LetterControllerTest extends CommonControllerTest {

    @Autowired
    private LetterService letterService;

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private LetterRepository letterRepository;

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
                .position(잠실_루터회관_정문_좌표)
                .build();
        final Player player = playerBuilder.init()
                .build();
        final Letter letter1 = letterBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();
        final Letter letter2 = letterBuilder.init()
                .position(역삼역_좌표)
                .build();

        final AuthToken generate = authTokenGenerator.generate(destination.getRegisteredPlayer().getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .param("latitude", "37.515446")
                .param("longitude", "127.102899")
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
    void 주변_쪽지를_모두_조회시_쪽지가_없다면_빈_리스트를_반환한다() {
        // given
        final Place destination = placeBuilder.init()
                .position(역삼역_좌표)
                .build();
        final Player player = playerBuilder.init()
                .build();
        final Letter letter1 = letterBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();
        final Letter letter2 = letterBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();

        final AuthToken generate = authTokenGenerator.generate(destination.getRegisteredPlayer().getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .param("latitude", "37.500845")
                .param("longitude", "127.036953")
                .when()
                .get("/letters/nearby")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<NearByLetterResponse> actual = extract.as(new TypeRef<>() {
        });
        final int statusCode = extract.statusCode();
        final List<NearByLetterResponse> expected = new ArrayList<>();

        assertSoftly(softly -> {
            softly.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                    .hasSize(0)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        });
    }

    @Test
    void 주변_쪽지를_모두_조회시_요청_파라미터에_빈값이_존재하면_예외를_발생시킨다() {
        // given
        final Place destination = placeBuilder.init()
                .position(역삼역_좌표)
                .build();

        final AuthToken generate = authTokenGenerator.generate(destination.getRegisteredPlayer().getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .param("latitude", "")
                .param("longitude", "127.036953")
                .when()
                .get("/letters/nearby")
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
                            .isEqualTo(expected);
                }
        );
    }
}
