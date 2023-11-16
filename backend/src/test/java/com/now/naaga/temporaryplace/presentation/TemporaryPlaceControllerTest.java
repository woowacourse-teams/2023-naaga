package com.now.naaga.temporaryplace.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.now.naaga.common.ControllerTest;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.presentation.dto.TemporaryPlaceResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class TemporaryPlaceControllerTest extends ControllerTest {

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

//    @Test
//    void 검수할_장소_등록_요청을_받으면_201_상태코드와_함께_추가된_검수할_장소_정보를_응답한다() throws FileNotFoundException {
//        //given
//        final Player player = playerBuilder.init()
//                .build();
//        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
//        final String accessToken = generate.getAccessToken();
//        final TemporaryPlace temporaryPlace = TEMPORARY_PLACE();
//
//        doReturn(new File("/임시경로", "이미지.png")).when(fileManager)
//                .save(any());
//        //when
//        final ExtractableResponse<Response> extract = given()
//                .log().all()
//                .multiPart(new MultiPartSpecBuilder(temporaryPlace.getName()).controlName("name").charset(StandardCharsets.UTF_8).build())
//                .multiPart(new MultiPartSpecBuilder(temporaryPlace.getDescription()).controlName("description").charset(StandardCharsets.UTF_8).build())
//                .multiPart(
//                        new MultiPartSpecBuilder(temporaryPlace.getPosition().getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName(
//                                "latitude").charset(StandardCharsets.UTF_8).build())
//                .multiPart(
//                        new MultiPartSpecBuilder(temporaryPlace.getPosition().getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName(
//                                "longitude").charset(StandardCharsets.UTF_8).build())
//                .multiPart(new MultiPartSpecBuilder(new FileInputStream(new File("src/test/java/com/now/naaga/temporaryplace/fixture/루터회관.png"))).controlName(
//                                "imageFile").charset(StandardCharsets.UTF_8)
//                        .fileName(
//                                "src/test/java/com/now/naaga/temporaryplace/fixture/루터회관.png")
//                        .mimeType(
//                                "image/png")
//                        .build())
//                .header("Authorization", "Bearer " + accessToken)
//                .when()
//                .post("/temporary-places")
//                .then()
//                .log().all()
//                .extract();
//        final int statusCode = extract.statusCode();
//        final String location = extract.header("Location");
//        final TemporaryPlaceResponse actual = extract.as(TemporaryPlaceResponse.class);
//        final TemporaryPlaceResponse expected = TemporaryPlaceResponse.from(temporaryPlace);
//
//        //then
//        assertSoftly(softAssertions -> {
//            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
//            softAssertions.assertThat(location).isEqualTo("/temporary-places/" + actual.id());
//            softAssertions.assertThat(actual)
//                    .usingRecursiveComparison()
//                    .ignoringFields("id", "imageUrl", "registeredPlayerId")
//                    .isEqualTo(expected);
//        });
//    }

    @Test
    void 검수할_장소_목록_조회_요청을_받으면_200_상태코드와_함께_검수할_장소_목록을_응답한다() throws FileNotFoundException, JsonProcessingException {
        //given
        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        //when
        final ExtractableResponse<Response> extract = RestAssured.given()
                                                                 .log().all()
                                                                 .auth().preemptive().basic(id, password)
                                                                 .when()
                                                                 .get("/temporary-places")
                                                                 .then()
                                                                 .log().all()
                                                                 .extract();
        final int statusCode = extract.statusCode();
        final List<TemporaryPlaceResponse> actual = extract.as(new TypeRef<>() {
        });
        final List<TemporaryPlaceResponse> expected = TemporaryPlaceResponse.convertToResponses(List.of(temporaryPlace));
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(expected)
                          .usingRecursiveComparison()
                          .isEqualTo(actual);
        });
    }

    void ID를_통한_삭제_요청이_성공하면_204_응답코드를_반환한다() {
        // given
        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        // when
        final ExtractableResponse<Response> extract = RestAssured.given()
                                                                 .log().all()
                                                                 .auth().preemptive().basic(id, password)
                                                                 .when()
                                                                 .delete("/temporary-places/{temporaryPlaceId}", temporaryPlace.getId())
                                                                 .then()
                                                                 .log().all()
                                                                 .extract();

        // then
        final int statusCode = extract.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
