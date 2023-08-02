package com.now.naaga.place;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.place.fixture.PlaceFixture;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import io.restassured.builder.MultiPartSpecBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import static com.now.naaga.player.fixture.PlayerFixture.PLAYER;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class GameControllerTest extends CommonControllerTest {

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        playerRepository.save(PLAYER());
    }

    @Test
    void 장소_추가_요청을_받으면_추가된_장소_정보를_응답한다() throws FileNotFoundException {
        //given

        //when
        PlaceResponse actual = given().
                log().all().
                multiPart(new MultiPartSpecBuilder("루터회관").controlName("name").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder("이곳은 루터회관이다.").controlName("description").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder("37.515411").controlName("latitude").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder("127.102848").controlName("longitude").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder(new FileInputStream(new File("src/test/java/com/now/naaga/place/fixture/루터회관.png"))).controlName("imageFile").charset(StandardCharsets.UTF_8).fileName("src/test/java/com/now/naaga/place/fixture/루터회관.png").mimeType("image/png").build()).
                auth().preemptive().basic("kokodak@koko.dak", "1234").
                when().
                post("/places").
                then().
                log().all().
                extract().
                body().as(PlaceResponse.class);
        //then
        assertThat(actual).
                usingRecursiveComparison().
                ignoringFields("id", "imageUrl").
                isEqualTo(PlaceFixture.PLACE_RESPONSE);
    }

}
