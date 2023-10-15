package com.now.naaga.like.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceLikeBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.like.application.dto.CountPlaceLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.presentation.dto.PlaceLikeCountResponse;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.exception.PlaceStatisticsExceptionType;
import com.now.naaga.player.domain.Player;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.now.naaga.like.exception.PlaceLikeExceptionType.NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceLikeControllerTest extends CommonControllerTest {

    private final PlaceBuilder placeBuilder;

    private final PlaceLikeBuilder placeLikeBuilder;

    private final PlaceStatisticsBuilder placeStatisticsBuilder;

    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public PlaceLikeControllerTest(final PlaceBuilder placeBuilder,
                                   final PlaceLikeBuilder placeLikeBuilder,
                                   final PlaceStatisticsBuilder placeStatisticsBuilder,
                                   final AuthTokenGenerator authTokenGenerator) {
        this.placeBuilder = placeBuilder;
        this.placeLikeBuilder = placeLikeBuilder;
        this.placeStatisticsBuilder = placeStatisticsBuilder;
        this.authTokenGenerator = authTokenGenerator;
    }

    @BeforeEach
    void setup() {
        super.setUp();
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
        final Member member = placeLike.getPlayer().getMember();
        final AuthToken generate = authTokenGenerator.generate(member, member.getId(), AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        //when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
