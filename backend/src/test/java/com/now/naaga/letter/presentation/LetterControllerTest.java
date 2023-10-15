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
import static com.now.naaga.common.fixture.PositionFixture.*;
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
}
