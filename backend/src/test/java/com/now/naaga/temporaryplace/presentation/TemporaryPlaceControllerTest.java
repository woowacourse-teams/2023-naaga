package com.now.naaga.temporaryplace.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ActiveProfiles("test")
class TemporaryPlaceControllerTest extends CommonControllerTest {

    @Autowired
    private TemporaryPlaceBuilder temporaryPlaceBuilder;

    @Value("${manager.id}")
    private String id;

    @Value("${manager.password}")
    private String password;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
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
