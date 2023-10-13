package com.now.naaga.letter.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.game.presentation.dto.GameResultResponse;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.application.dto.FindNearByLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.NearByLetterResponse;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.common.fixture.PositionFixture.역삼역_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
                .param("latitude", "37.514258")
                .param("longitude", "127.100883")
                .when()
                .get("/letters/nearby")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then

        final List<NearByLetterResponse> actual = extract.as(new TypeRef<>() {
        });

        System.out.println("후후후후ㅜ후: "+extract.response().body().prettyPrint());
        final int statusCode = extract.statusCode();
        final FindNearByLetterCommand findNearByLetterCommand = new FindNearByLetterCommand(잠실_루터회관_정문_좌표);
        final List<Letter> letterList = letterService.findNearByLetters(findNearByLetterCommand);
        System.out.println("그렇다면 여기는: "+letterList.size());
        final List<NearByLetterResponse> expected = letterList.stream()
                .map(NearByLetterResponse::from)
                .collect(Collectors.toList());

        assertSoftly(softly -> {
            softly.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(actual)
                    .hasSize(1)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        });
    }
}
