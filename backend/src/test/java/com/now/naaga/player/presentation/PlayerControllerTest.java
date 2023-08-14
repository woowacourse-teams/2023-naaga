package com.now.naaga.player.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.common.CommonControllerTest;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.RankResponse;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class PlayerControllerTest extends CommonControllerTest {

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void 멤버의_랭크를_조회한다() {
        // given
        Player chae = playerRepository.save(new Player("채채", new Score(15), new Member("chaechae@woo.com")));
        playerRepository.save(new Player("이레", new Score(17), new Member("irea@woo.com")));
        playerRepository.save(new Player("채리", new Score(20), new Member("cherry@woo.com")));


        final Long memberId = chae.getMember().getId();
        final AuthToken generate = authTokenGenerator.generate(memberId);
        final String accessToken = generate.getAccessToken();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/ranks/my")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final RankResponse rankResponse = response.as(RankResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(rankResponse.getPlayer().getId()).isEqualTo(1L);
            softly.assertThat(rankResponse.getPlayer().getNickname()).isEqualTo("채채");
            softly.assertThat(rankResponse.getPlayer().getTotalScore()).isEqualTo(15);
            softly.assertThat(rankResponse.getRank()).isEqualTo(3);
            softly.assertThat(rankResponse.getPercentage()).isEqualTo(100);
        });
    }

    @Test
    void 모든_맴버의_랭크를_조회한다() {
        // given
        playerRepository.save(new Player("채채", new Score(15), new Member("chaechae@woo.com")));
        playerRepository.save(new Player("이레", new Score(17), new Member("irea@woo.com")));
        playerRepository.save(new Player("채리", new Score(20), new Member("cherry@woo.com")));
        // when
        final ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/ranks?sort-by=rank&order=ascending")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<RankResponse> rankResponseList = response.jsonPath().getList(".", RankResponse.class);
        assertThat(rankResponseList).hasSize(3);
        final RankResponse firstRank = rankResponseList.get(0);
        assertThat(firstRank.getPlayer().getNickname()).isEqualTo("채리");
        assertThat(firstRank.getRank()).isEqualTo(1);

        final RankResponse secondRank = rankResponseList.get(1);
        assertThat(secondRank.getPlayer().getNickname()).isEqualTo("이레");
        assertThat(secondRank.getRank()).isEqualTo(2);

        final RankResponse thirdRank = rankResponseList.get(2);
        assertThat(thirdRank.getPlayer().getNickname()).isEqualTo("채채");
        assertThat(thirdRank.getRank()).isEqualTo(3);
    }
}
