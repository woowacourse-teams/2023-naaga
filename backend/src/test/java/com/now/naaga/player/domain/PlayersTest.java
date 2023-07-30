package com.now.naaga.player.domain;

import com.now.naaga.member.domain.Member;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlayersTest {

    private List<Player> playerList = new ArrayList<>();
    private Player targetPlayer;

    @BeforeEach
    void setUp() {
        final Member member1 = new Member(1L, "chaechae@woo.com", "1234");
        final Member member2 = new Member(2L, "irea@woo.com", "1234");
        final Member member3 = new Member("3L,cherry@woo.com", "1234");

        final Player player1 = new Player(1L, "채채", new Score(15), member1);
        final Player player2 = new Player(2L, "이레", new Score(17), member2);
        final Player player3 = new Player(3L, "채리", new Score(20), member3);

        targetPlayer = player3;

        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
    }

    @Test
    void 플레이어를_받아_인덱스를_계산한다() {
        // given
        final Players players = new Players(playerList);

        // when
        int rank = players.calculateRank(targetPlayer);

        // then
        assertThat(rank).isEqualTo(3);
    }

    @Test
    void 플레이어의_링크를_받아_상위_퍼센트를_계산한다() {
        // given
        final Players players = new Players(playerList);

        // when
        int topPercent = players.calculateTopPercent(1);

        // then
        assertThat(topPercent).isEqualTo(33);
    }
}
