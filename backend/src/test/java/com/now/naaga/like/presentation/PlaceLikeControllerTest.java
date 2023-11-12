package com.now.naaga.like.presentation;

import static com.now.naaga.like.exception.PlaceLikeExceptionType.ALREADY_APPLIED_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.now.naaga.common.ControllerTest;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.like.domain.MyPlaceLikeType;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.presentation.dto.ApplyPlaceLikeRequest;
import com.now.naaga.like.presentation.dto.CheckMyPlaceLikeResponse;
import com.now.naaga.like.presentation.dto.PlaceLikeCountResponse;
import com.now.naaga.like.presentation.dto.PlaceLikeResponse;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.exception.PlaceStatisticsExceptionType;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


@SuppressWarnings("NonAsciiCharacters")
class PlaceLikeControllerTest extends ControllerTest {

    @Test
    void 좋아요_등록이_성공하면_201_응답을_반환한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        placeStatisticsBuilder.init()
                              .place(place)
                              .build();

        final ApplyPlaceLikeRequest applyPlaceLikeRequest = new ApplyPlaceLikeRequest(PlaceLikeType.LIKE);

        // when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(applyPlaceLikeRequest)
                .when()
                .post("/places/{placeId}/likes", place.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final PlaceLikeResponse actual = extract.as(PlaceLikeResponse.class);
        final PlaceLikeResponse expected = new PlaceLikeResponse(null,
                                                                 player.getId(),
                                                                 place.getId(),
                                                                 PlaceLikeType.LIKE);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .ignoringExpectedNullFields()
                          .isEqualTo(expected);
        });
    }

    @Test
    void 좋아요_등록_요청시_중복되는_좋아요_타입에_대한_입력이_들어오면_예외_응답과_400_응답을_반환한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        placeStatisticsBuilder.init()
                              .place(place)
                              .build();

        placeLikeBuilder.init()
                        .player(player)
                        .place(place)
                        .placeLikeType(PlaceLikeType.LIKE)
                        .build();

        final ApplyPlaceLikeRequest applyPlaceLikeRequest = new ApplyPlaceLikeRequest(PlaceLikeType.LIKE);

        // when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(applyPlaceLikeRequest)
                .when()
                .post("/places/{placeId}/likes", place.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final ExceptionResponse expected = new ExceptionResponse(ALREADY_APPLIED_TYPE.errorCode(),
                                                                 ALREADY_APPLIED_TYPE.errorMessage());
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .isEqualTo(expected);
        });
    }

    @Test
    void 좋아요_삭제를_성공하면_204응답을_한다() {
        //given
        final Place place = placeBuilder.init()
                                        .build();
        final PlaceLike placeLike = placeLikeBuilder.init()
                                                    .place(place)
                                                    .build();
        placeStatisticsBuilder.init()
                              .place(place)
                              .build();

        //when
        final ExtractableResponse<Response> extract = given(placeLike.getPlayer())
                .contentType(ContentType.JSON)
                .pathParam("placeId", place.getId())
                .when()
                .delete("/places/{placeId}/likes/my")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        assertThat(statusCode).isEqualTo(NO_CONTENT.value());
    }

    @Test
    void 좋아요가_눌려있을_때_나의_좋아요_여부를_조회하면_해당_좋아요_타입_응답과_200_상태코드를_반환한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        final PlaceLikeType myType = PlaceLikeType.LIKE;

        placeLikeBuilder.init()
                        .player(player)
                        .place(place)
                        .placeLikeType(myType)
                        .build();

        // when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/places/{placeId}/likes/my", place.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final CheckMyPlaceLikeResponse actual = extract.as(CheckMyPlaceLikeResponse.class);
        final CheckMyPlaceLikeResponse expected = new CheckMyPlaceLikeResponse(MyPlaceLikeType.from(myType));
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .isEqualTo(expected);
        });
    }

    @Test
    void 아무것도_눌려있지_않을_때_나의_좋아요_여부를_조회하면_NONE_타입_응답과_200_상태코드를_반환한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        // when
        final ExtractableResponse<Response> extract = given(player)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/places/{placeId}/likes/my", place.getId())
                .then().log().all()
                .extract();

        // then
        final int statusCode = extract.statusCode();
        final CheckMyPlaceLikeResponse actual = extract.as(CheckMyPlaceLikeResponse.class);
        final CheckMyPlaceLikeResponse expected = new CheckMyPlaceLikeResponse(MyPlaceLikeType.NONE);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .isEqualTo(expected);
        });
    }

    @Test
    void 특정_장소의_좋아요_수를_응답하고_응답의_상태코드는_200이다() {
        //given
        final Long expected = 123L;
        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .likeCount(expected)
                                                                      .build();
        final Long placeId = placeStatistics.getPlace().getId();

        //when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .pathParam("placeId", placeId)
                .when()
                .get("/places/{placeId}/likes/count")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final PlaceLikeCountResponse actual = extract.as(PlaceLikeCountResponse.class);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(OK.value());
            softAssertions.assertThat(actual.placeLikeCount()).isEqualTo(expected);
        });
    }

    @Test
    void 좋아요_수를_조회할_때_장소_통계가_없으면_400_예외를_발생한다() {
        //given & when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .pathParam("placeId", 100)
                .when()
                .get("/places/{placeId}/likes/count")
                .then().log().all()
                .extract();

        //then
        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(NOT_FOUND.value());
            softAssertions.assertThat(actual.getCode()).isEqualTo(PlaceStatisticsExceptionType.NOT_FOUND.errorCode());
            softAssertions.assertThat(actual.getMessage()).isEqualTo(PlaceStatisticsExceptionType.NOT_FOUND.errorMessage());
        });
    }
}
