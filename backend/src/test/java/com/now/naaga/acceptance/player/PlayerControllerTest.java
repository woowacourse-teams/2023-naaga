package com.now.naaga.acceptance.player;

import com.now.naaga.acceptance.ControllerTest;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.RankResponse;
import com.now.naaga.score.domain.Score;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlayerControllerTest extends ControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        final Member saveMember1 = memberRepository.save(new Member("chaechae@woo.com", "1234"));
        final Member saveMember2 = memberRepository.save(new Member("irea@woo.com", "1234"));
        final Member saveMember3 = memberRepository.save(new Member("cherry@woo.com", "1234"));

        playerRepository.save(new Player("채채", new Score(15), saveMember1));
        playerRepository.save(new Player("이레", new Score(17), saveMember2));
        playerRepository.save(new Player("채리", new Score(20), saveMember3));
    }

    @Test
    void 멤버의_랭크를_조회한다() {
        // given
        final String encodedCredentials = calculateEncodedCredentials();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Basic " + encodedCredentials)
                .when().get("/ranks/my")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final RankResponse rankResponse = response.as(RankResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(rankResponse.getId()).isEqualTo(1L);
            softly.assertThat(rankResponse.getNickname()).isEqualTo("채채");
            softly.assertThat(rankResponse.getTotalScore()).isEqualTo(15);
            softly.assertThat(rankResponse.getRank()).isEqualTo(3);
            softly.assertThat(rankResponse.getTopPercent()).isEqualTo(100);
        });
    }

    @Test
    void 모든_맴버의_랭크를_조회한다() {
        // given
        final String encodedCredentials = calculateEncodedCredentials();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Basic " + encodedCredentials)
                .when().get("/ranks?sortBy=rank&order=descending")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        final List<RankResponse> rankResponseList = response.jsonPath().getList(".", RankResponse.class);
        assertThat(rankResponseList).hasSize(3);
        final RankResponse firstRank = rankResponseList.get(0);
        assertThat(firstRank.getNickname()).isEqualTo("채리");
        assertThat(firstRank.getRank()).isEqualTo(1);

        final RankResponse secondRank = rankResponseList.get(1);
        assertThat(secondRank.getNickname()).isEqualTo("이레");
        assertThat(secondRank.getRank()).isEqualTo(2);

        final RankResponse thirdRank = rankResponseList.get(2);
        assertThat(thirdRank.getNickname()).isEqualTo("채채");
        assertThat(thirdRank.getRank()).isEqualTo(3);
    }


    private String calculateEncodedCredentials() {
        final String username = "chaechae@woo.com";
        final String password = "1234";
        final String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
