package com.now.naaga.like.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceLikeBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
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
import static org.springframework.http.HttpStatus.NO_CONTENT;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceLikeControllerTest extends CommonControllerTest {

    PlayerBuilder playerBuilder;

    PlaceBuilder placeBuilder;

    PlaceLikeBuilder placeLikeBuilder;

    AuthTokenGenerator authTokenGenerator;

    @BeforeEach
    void setup() {
        super.setUp();
    }

    @Autowired
    public PlaceLikeControllerTest(final PlayerBuilder playerBuilder,
                                   final PlaceBuilder placeBuilder,
                                   final PlaceLikeBuilder placeLikeBuilder,
                                   final AuthTokenGenerator authTokenGenerator) {
        this.playerBuilder = playerBuilder;
        this.placeBuilder = placeBuilder;
        this.placeLikeBuilder = placeLikeBuilder;
        this.authTokenGenerator = authTokenGenerator;
    }

    @Test
    void 좋아요_삭제를_성공하면_204응답을_한다() {
        //given
        final PlaceLike placeLike = placeLikeBuilder.init()
                .build();
        final Place place = placeLike.getPlace();
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
    void 좋아요_누른적_없는_장소에_좋아요_삭제하면_예외응답을_한다() {
        //given
        final Place place = placeBuilder.init()
                .build();
        final Player player = playerBuilder.init()
                .build();
        final Member member = player.getMember();
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
        final ExceptionResponse exceptionResponse = extract.body().as(ExceptionResponse.class);
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(new ExceptionResponse(NOT_EXIST.errorCode(), NOT_EXIST.errorMessage()));
        });
    }
}
