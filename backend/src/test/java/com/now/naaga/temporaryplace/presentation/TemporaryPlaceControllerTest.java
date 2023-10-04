package com.now.naaga.temporaryplace.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.common.exception.ExceptionResponse;
import com.now.naaga.common.infrastructure.FileManager;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.exception.TemporaryPlaceException;
import com.now.naaga.temporaryplace.exception.TemporaryPlaceExceptionType;
import com.now.naaga.temporaryplace.presentation.dto.TemporaryPlaceResponse;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

import static com.now.naaga.common.fixture.PlaceFixture.PLACE;
import static com.now.naaga.common.fixture.PositionFixture.서울_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.TemporaryPlaceFixture.TEMPORARY_PLACE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TemporaryPlaceControllerTest extends CommonControllerTest {

    @Autowired
    private TemporaryPlaceBuilder temporaryPlaceBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @MockBean
    private FileManager<MultipartFile> fileManager;

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

    @Test
    void 검수할_장소_등록_요청을_받으면_추가된_검수할_장소_정보를_응답한다() throws FileNotFoundException {
        //given
        final Player player = playerBuilder.init()
                .build();
        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        final TemporaryPlace temporaryPlace = TEMPORARY_PLACE();

        when(fileManager.save(any())).thenReturn(new File("/임시경로", "이미지.png"));

        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .multiPart(new MultiPartSpecBuilder(temporaryPlace.getName()).controlName("name").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(temporaryPlace.getDescription()).controlName("description").charset(StandardCharsets.UTF_8).build())
                .multiPart(
                        new MultiPartSpecBuilder(temporaryPlace.getPosition().getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName(
                                "latitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(
                        new MultiPartSpecBuilder(temporaryPlace.getPosition().getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName(
                                "longitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(new FileInputStream(new File("src/test/java/com/now/naaga/temporaryplace/fixture/루터회관.png"))).controlName(
                                "imageFile").charset(StandardCharsets.UTF_8)
                        .fileName(
                                "src/test/java/com/now/naaga/temporaryplace/fixture/루터회관.png")
                        .mimeType(
                                "image/png")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("/temporary-places")
                .then()
                .log().all()
                .extract();
        final int statusCode = extract.statusCode();
        final String location = extract.header("Location");
        final TemporaryPlaceResponse actual = extract.as(TemporaryPlaceResponse.class);
        final TemporaryPlaceResponse expected = TemporaryPlaceResponse.from(temporaryPlace);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
            softAssertions.assertThat(location).isEqualTo("/temporary-places/" + actual.id());
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "imageUrl")
                    .isEqualTo(expected);
        });

    }

    @Test
    void 검수할_장소_추가_요청_시_주변_20미터_내에_이미_등록된_목적지가_있다면_예외를_응답한다() throws FileNotFoundException {
        //given
        final Player player = playerBuilder.init()
                .build();
        final Place place = placeBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();
        final TemporaryPlace temporaryPlace = TEMPORARY_PLACE();

        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        when(fileManager.save(any())).thenReturn(new File("/임시경로", "이미지.png"));

        //when
        final ExtractableResponse<Response> extract = given()
                .log().all()
                .multiPart(new MultiPartSpecBuilder(temporaryPlace.getName()).controlName("name").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(temporaryPlace.getDescription()).controlName("description").charset(StandardCharsets.UTF_8).build())
                .multiPart(
                        new MultiPartSpecBuilder(place.getPosition().getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName(
                                "latitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(
                        new MultiPartSpecBuilder(place.getPosition().getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()).controlName(
                                "longitude").charset(StandardCharsets.UTF_8).build())
                .multiPart(new MultiPartSpecBuilder(new FileInputStream(new File("src/test/java/com/now/naaga/temporaryplace/fixture/루터회관.png"))).controlName(
                                "imageFile").charset(StandardCharsets.UTF_8)
                        .fileName(
                                "src/test/java/com/now/naaga/temporaryplace/fixture/루터회관.png")
                        .mimeType(
                                "image/png")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("/temporary-places")
                .then()
                .log().all()
                .extract();

        final int statusCode = extract.statusCode();
        final ExceptionResponse actual = extract.as(ExceptionResponse.class);
        final TemporaryPlaceExceptionType exceptionType = TemporaryPlaceExceptionType.ALREADY_EXIST_NEARBY;
        final ExceptionResponse expected = new ExceptionResponse(exceptionType.errorCode(), exceptionType.errorMessage());

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softAssertions.assertThat(expected)
                    .usingRecursiveComparison()
                    .isEqualTo(actual);
        });
    }
}
