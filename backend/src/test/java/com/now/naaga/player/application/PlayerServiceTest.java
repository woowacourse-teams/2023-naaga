package com.now.naaga.player.application;

import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Players;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        final Member saveMember1 = memberRepository.save(new Member("chaechae@woo.com", "1234"));
        final Member saveMember2 = memberRepository.save(new Member("irea@woo.com", "1234"));
        final Member saveMember3 = memberRepository.save(new Member("cherry@woo.com", "1234"));

        playerRepository.save(new Player("채채", new Score(15), saveMember1));
        playerRepository.save(new Player("이레", new Score(17), saveMember2));
        playerRepository.save(new Player("채리", new Score(20), saveMember3));
    }

    @Test
    void 모든_플레이어를_점수_기준으로_내림차순_정렬을_하여_조회한다() {
        // when
        final Players players = playerService.findAllPlayersByRanksAscending();

        // then
        assertSoftly(softly -> {
            softly.assertThat(players.getPlayers().get(0).getNickname()).isEqualTo("채리");
            softly.assertThat(players.getPlayers()).hasSize(3);
        });
    }

    @Test
    void 멤버Request로_해당_플레이어를_조회한다() {
        // when
        final Player player = playerService.getRankAndTopPercent(new MemberCommand("cherry@woo.com", "1234"));

        // then
        assertThat(player.getNickname()).isEqualTo("채리");
    }
}
