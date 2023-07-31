package com.now.naaga.place;

import com.now.naaga.place.fixture.PlaceFixture;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlaceIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    void 장소_추가_요청을_받으면_추가된_장소_정보를_응답한다() {
        PlaceResponse actual = given().
                log().all().
                port(port).
                contentType(ContentType.MULTIPART).
                multiPart(new MultiPartSpecBuilder("루터회관").controlName("name").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder("이곳은 루터회관이다.").controlName("description").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder("37.515411").controlName("latitude").charset(StandardCharsets.UTF_8).build()).
                multiPart(new MultiPartSpecBuilder("127.102848").controlName("longitude").charset(StandardCharsets.UTF_8).build()).
                multiPart("imageFile", new File("src/test/java/com/now/naaga/place/fixture/루터회관.png")).
                auth().preemptive().basic("111@woowa.com", "1111").
                when().
                post("/places").
                then().
                log().all().
                extract().
                body().as(PlaceResponse.class);

        assertThat(actual).
                usingRecursiveComparison().
                ignoringFields("id", "imageUrl").
                isEqualTo(PlaceFixture.PLACE_RESPONSE);
    }
}
