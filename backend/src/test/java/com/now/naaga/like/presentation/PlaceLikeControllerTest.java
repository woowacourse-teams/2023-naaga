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
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
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

    private final PlayerBuilder playerBuilder;

    private final PlaceBuilder placeBuilder;

    private final PlaceLikeBuilder placeLikeBuilder;

    private final PlaceStatisticsBuilder placeStatisticsBuilder;

    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public PlaceLikeControllerTest(final PlayerBuilder playerBuilder,
                                   final PlaceBuilder placeBuilder,
                                   final PlaceLikeBuilder placeLikeBuilder,
                                   final PlaceStatisticsBuilder placeStatisticsBuilder,
                                   final AuthTokenGenerator authTokenGenerator) {
        this.playerBuilder = playerBuilder;
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
}
