package com.now.naaga.player.presentation;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PlayerInfoControllerTest extends CommonControllerTest {
    
    
    @Autowired
    private AuthTokenGenerator authTokenGenerator;
    
    @Autowired
    private PlayerBuilder playerBuilder;
    
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }
    
    @Test
    void 플레이어_정보를_정상적으로_조회한다() {
        //given
        final Player player = playerBuilder.init()
                                           .build();
        
        
        final AuthToken generate = authTokenGenerator.generate(player.getMember(), 1L, AuthType.KAKAO);
        final String accessToken = generate.getAccessToken();
        
        //when
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/profiles/my")
                .then().log().all()
                .extract();
        
        //then
        final int statusCode = extract.statusCode();
        final PlayerResponse actual = extract.as(PlayerResponse.class);
        final PlayerResponse expected = PlayerResponse.from(player);
        
        assertSoftly(softAssertions -> {
                    softAssertions.assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(actual)
                                  .usingRecursiveComparison()
                                  .isEqualTo(expected);
                }
        );
    }
    
}
