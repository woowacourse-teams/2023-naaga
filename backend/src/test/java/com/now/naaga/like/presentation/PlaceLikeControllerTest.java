package com.now.naaga.like.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceLikeBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.presentation.dto.ApplyPlaceLikeRequest;
import com.now.naaga.like.presentation.dto.PlaceLikeResponse;
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
import org.springframework.http.MediaType;

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

        final Member member = player.getMember();
        final AuthToken generate = authTokenGenerator.generate(member, member.getId(), AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
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
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .ignoringExpectedNullFields()
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
