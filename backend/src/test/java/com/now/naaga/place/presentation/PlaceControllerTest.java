package com.now.naaga.place.presentation;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.common.ControllerTest;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class PlaceControllerTest extends ControllerTest {

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

//    @Test
//    void 장소_등록_요청이_성공하면_201_상태코드와_생성된_장소를_응답한다() {
//        // given
//        final Player player = playerBuilder.init()
//                                           .build();
//
//        final CreatePlaceRequest createPlaceRequest = new CreatePlaceRequest("루터회관",
//                                                                             "이곳은 역사와 전통이 깊은 루터회관입니다.",
//                                                                             1.234,
//                                                                             5.6789,
//                                                                             "image/url",
//                                                                             player.getId(),
//                                                                             1L);
//
//        // when
//        final ExtractableResponse<Response> extract = given()
//                .log().all()
//                .auth().preemptive().basic(id, password)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(createPlaceRequest)
//                .when()
//                .post("/places")
//                .then()
//                .log().all()
//                .extract();
//
//        // then
//        final int statusCode = extract.statusCode();
//        final PlaceResponse actual = extract.as(PlaceResponse.class);
//        final Position position = Position.of(createPlaceRequest.latitude(),
//                                              createPlaceRequest.longitude());
//        final PlaceResponse expected = new PlaceResponse(getIdFromLocationHeader(extract),
//                                                         createPlaceRequest.name(),
//                                                         CoordinateResponse.from(position),
//                                                         createPlaceRequest.imageUrl(),
//                                                         createPlaceRequest.description());
//        assertSoftly(softAssertions -> {
//            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
//            softAssertions.assertThat(actual)
//                          .usingRecursiveComparison()
//                          .isEqualTo(expected);
//        });
//    }

    @Test
    void 장소를_아이디로_조회한다() {
        //given
        final Place place = placeBuilder.init()
                                        .build();

        final AuthToken generate = authTokenGenerator.generate(place.getRegisteredPlayer().getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .pathParam("placeId", place.getId())
                .header("Authorization", "Bearer " + accessToken)
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
        final Place place = placeBuilder.init()
                                        .build();

        final AuthToken generate = authTokenGenerator.generate(place.getRegisteredPlayer().getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
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
        final List<PlaceResponse> actual = objectMapper.readValue(jsonResponse, new TypeReference<List<PlaceResponse>>() {
        });
        final List<PlaceResponse> expected = PlaceResponse.convertToPlaceResponses(List.of(place));
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(expected)
                          .usingRecursiveComparison()
                          .isEqualTo(actual);
        });
    }
}
