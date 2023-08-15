package com.now.naaga.player.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@Transactional
@SpringBootTest
class PlayerRepositoryTest {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 맴버아이디로_플레이어를_조회한다() {
        // given
        final Player savePlayer = playerRepository.save(new Player("채채", new Score(15), new Member("chaechae@woo.com")));

        // when
        final Player foundPlayer = playerRepository.findByMemberIdAndDeletedFalse(savePlayer.getMember().getId()).get(0);

        // then
        assertThat(foundPlayer).isEqualTo(savePlayer);
    }
}
