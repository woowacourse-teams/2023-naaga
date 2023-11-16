package com.now.naaga.player.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.now.naaga.common.ServiceTest;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.application.dto.AddScoreCommand;
import com.now.naaga.player.application.dto.EditPlayerNicknameCommand;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.score.domain.Score;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

class PlayerServiceTest extends ServiceTest {

    @Autowired
    private PlayerService playerService;

    private List<Player> playerList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        final Member saveMember1 = memberRepository.save(new Member("chaechae@woo.com"));
        final Member saveMember2 = memberRepository.save(new Member("irea@woo.com"));
        final Member saveMember3 = memberRepository.save(new Member("cherry@woo.com"));

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
            softly.assertThat(rank.getPercentage()).isEqualTo(33);
        });
    }

    @Test
    void 플레이어의_점수를_추가한다() {
        // given
        final Player player = playerBuilder.init().build();
        final Score beforeScore = player.getTotalScore();
        final Score addedScore = new Score(10);
        final AddScoreCommand addScoreCommand = new AddScoreCommand(player.getId(), addedScore);

        // when
        playerService.addScore(addScoreCommand);

        // then
        final Player afterPlayer = playerRepository.findById(player.getId()).get();
        assertThat(afterPlayer.getTotalScore()).isEqualTo(beforeScore.plus(addedScore));
    }

    @Test
    void 플레이어의_닉네임을_변경한다() {
        //given
        final String expected = "변경 닉네임";
        final Player player = playerBuilder.init().build();
        final EditPlayerNicknameCommand editPlayerNicknameCommand = new EditPlayerNicknameCommand(player.getId(), expected);

        //when
        final Player actual = playerService.editPlayerNickname(editPlayerNicknameCommand);

        //then
        assertThat(actual.getNickname()).isEqualTo(expected);
    }

    @Test
    void 플레이어의_닉네임을_변경_시_불가능한_닉네임을_입력하면_예외를_발생한다() {
        //given
        final String expected = "변경 닉네임!!";
        final Player player = playerBuilder.init().build();
        final EditPlayerNicknameCommand editPlayerNicknameCommand = new EditPlayerNicknameCommand(player.getId(), expected);

        //when & then
        assertThatThrownBy(() -> playerService.editPlayerNickname(editPlayerNicknameCommand))
                .isInstanceOf(PlayerException.class);
    }
}
