package com.now.naaga.player.persistence.repository;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class PlayerRepositoryTest {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 맴버아이디로_플레이어를_조회한다() {
        // given
        final Member saveMember = memberRepository.save(new Member("chaechae@woo.com", "1234"));
        final Member foundMember = memberRepository.findByEmail(saveMember.getEmail()).get();
        final Player savePlayer = playerRepository.save(new Player("채채", new Score(15), foundMember));

        // when
        final Player foundPlayer = playerRepository.findByMemberId(saveMember.getId()).get(0);

        // then
        assertThat(foundPlayer).isEqualTo(savePlayer);
    }
}
