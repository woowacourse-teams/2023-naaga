package com.now.naaga.game.presentation;

import static com.now.naaga.game.fixture.GameFixture.SEOUL_TO_JEJU_GAME;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.CreateHintRequest;
import com.now.naaga.game.presentation.dto.DirectionResponse;
import com.now.naaga.game.presentation.dto.HintResponse;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.fixture.PlaceFixture;
import com.now.naaga.place.fixture.PositionFixture;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.place.presentation.dto.CoordinateResponse;
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

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class GameControllerTest extends CommonControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlaceRepository placeRepository;

    private Game game;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        final Place place = placeRepository.save(PlaceFixture.JEJU_PLACE());
        game = gameRepository.save(SEOUL_TO_JEJU_GAME(place));
    }

    @Test
    void 힌트를_생성한다() {
        // given & when
        final CoordinateRequest SEOUL_COORDINATE = new CoordinateRequest(37.535978, 126.981654);

        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .auth().preemptive().basic("kokodak@koko.dak", "1234")
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
                DirectionResponse.from(Direction.SOUTH),
                CoordinateResponse.from(PositionFixture.SEOUL_POSITION()));

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
            softAssertions.assertThat(location).isEqualTo("/games/" + game.getId() + "/hints/" + actual.id());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        });
    }
}