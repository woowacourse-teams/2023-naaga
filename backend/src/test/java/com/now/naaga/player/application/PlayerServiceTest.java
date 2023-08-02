package com.now.naaga.player.application;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@SpringBootTest
@Sql("/truncate.sql")
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MemberRepository memberRepository;

    private List<Player> playerList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        final Member saveMember1 = memberRepository.save(new Member("chaechae@woo.com", "1234"));
        final Member saveMember2 = memberRepository.save(new Member("irea@woo.com", "1234"));
        final Member saveMember3 = memberRepository.save(new Member("cherry@woo.com", "1234"));

        final Player player1 = playerRepository.save(new Player("채채", new Score(15), saveMember1));
        final Player player2 = playerRepository.save(new Player("이레", new Score(17), saveMember2));
        final Player player3 = playerRepository.save(new Player("채리", new Score(20), saveMember3));

        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
    }

    @Test
    void 모든_플레이어를_점수_기준으로_내림차순_정렬을_하여_조회한다() {
        // when
        final List<Rank> ranks = playerService.getAllPlayersByRanksAscending();

        // then
        assertSoftly(softly -> {
            softly.assertThat(ranks.get(0).getPlayer().getNickname()).isEqualTo("채리");
            softly.assertThat(ranks.get(1).getPlayer().getNickname()).isEqualTo("이레");
            softly.assertThat(ranks.get(2).getPlayer().getNickname()).isEqualTo("채채");
            softly.assertThat(ranks).hasSize(3);
        });
    }

    @Test
    void 플레이어의_랭크점수를_확인한다() {
        // when
        final Rank rank = playerService.getRankAndTopPercent(new PlayerRequest(3L));

        // then
        assertSoftly(softly -> {
            softly.assertThat(rank.getPlayer().getNickname()).isEqualTo("채리");
            softly.assertThat(rank.getTopPercent()).isEqualTo(33);
        });
    }
}
