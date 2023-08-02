package com.now.naaga.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.place.fixture.PlaceFixture;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.now.naaga.place.fixture.PlaceFixture.JEJU_PLACE;
import static com.now.naaga.place.fixture.PlaceFixture.SEOUL_PLACE;
import static com.now.naaga.place.fixture.PositionFixture.JEJU_POSITION;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static com.now.naaga.player.fixture.PlayerFixture.PLAYER;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class PlaceControllerTest extends CommonControllerTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void 장소_추가_요청을_받으면_추가된_장소_정보를_응답한다() throws FileNotFoundException {
        //given
        final Player player = playerRepository.save(PLAYER());
        final Place SEOUL = SEOUL_PLACE();
        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .multiPart(new MultiPartSpecBuilder(SEOUL.getName()).controlName("name").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(SEOUL.getDescription()).controlName("description").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(SEOUL.getPosition().getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName("latitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(SEOUL.getPosition().getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName("longitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(new FileInputStream(new File("src/test/java/com/now/naaga/place/fixture/루터회관.png"))).controlName("imageFile").charset(StandardCharsets.UTF_8).fileName("src/test/java/com/now/naaga/place/fixture/루터회관.png").mimeType("image/png").build())
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .when()
                .post("/places")
                .then()
                .log().all()
                .extract();
        final int statusCode = extract.statusCode();
        final String location = extract.header("Location");
        final PlaceResponse actual = extract.as(PlaceResponse.class);
        final PlaceResponse expected = PlaceResponse.from(SEOUL);
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
            softAssertions.assertThat(location).isEqualTo("/places/" + actual.id());
            softAssertions.assertThat(expected)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "imageUrl")
                    .isEqualTo(actual);
        });

    }

    @Test
    void 장소_추가_요청_시_주변_20미터_내에_이미_등록된_목적지가_있다면_예외를_응답한다() throws FileNotFoundException {
        //given
        final Place place = placeRepository.save(SEOUL_PLACE());
        final Player player = place.getRegisteredPlayer();
        final Place SEOUL = SEOUL_PLACE();
        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .multiPart(new MultiPartSpecBuilder(SEOUL.getName()).controlName("name").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(SEOUL.getDescription()).controlName("description").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(SEOUL.getPosition().getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName("latitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(SEOUL.getPosition().getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName("longitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(new FileInputStream(new File("src/test/java/com/now/naaga/place/fixture/루터회관.png"))).controlName("imageFile").charset(StandardCharsets.UTF_8).fileName("src/test/java/com/now/naaga/place/fixture/루터회관.png").mimeType("image/png").build())
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .when()
                .post("/places")
                .then()
                .log().all()
                .extract();
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        PlaceExceptionType exceptionType = PlaceExceptionType.ALREADY_EXIST_NEARBY;
        ExceptionResponse expected = new ExceptionResponse(exceptionType.errorCode(), exceptionType.errorMessage());
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softAssertions.assertThat(expected)
                    .usingRecursiveComparison()
                    .isEqualTo(actual);
        });
    }

    @Test
    void 장소를_아이디로_조회한다() {
        //given
        final Place place = placeRepository.save(SEOUL_PLACE());
        final Player player = place.getRegisteredPlayer();
        final Place SEOUL = SEOUL_PLACE();
        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .pathParam("placeId", place.getId())
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .when()
                .get("/places/{placeId}")
                .then()
                .log().all()
                .extract();
        final int statusCode = extract.statusCode();
        final PlaceResponse actual = extract.as(PlaceResponse.class);
        final PlaceResponse expected = PlaceResponse.from(place);
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(expected)
                    .usingRecursiveComparison()
                    .isEqualTo(actual);
        });
    }

    @Test
    void 회원이_등록한_장소를_조회한다() throws JsonProcessingException {
        //given
        final Place SEOUL = placeRepository.save(SEOUL_PLACE());
        final Player player = SEOUL.getRegisteredPlayer();
        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .auth().preemptive().basic(player.getMember().getEmail(), player.getMember().getPassword())
                .queryParam("sort-by", "time")
                .queryParam("order", "descending")
                .when()
                .get("/places")
                .then()
                .log().all()
                .extract();
        final int statusCode = extract.statusCode();
        final String jsonResponse = extract.body().asString();
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<PlaceResponse> actual = objectMapper.readValue(jsonResponse, new TypeReference<List<PlaceResponse>>() {});
        final List<PlaceResponse> expected = PlaceResponse.convertToPlaceResponses(List.of(SEOUL));
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(expected)
                    .usingRecursiveComparison()
                    .isEqualTo(actual);
        });
    }
}