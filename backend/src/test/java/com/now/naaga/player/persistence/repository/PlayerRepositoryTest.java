package com.now.naaga.player.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.AbstractTest;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class PlayerRepositoryTest extends AbstractTest {

    @Test
    void 맴버아이디로_플레이어를_조회한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        // when
        final Player foundPlayer = playerRepository.findByMemberId(player.getMember().getId()).get(0);

        // then
        assertThat(foundPlayer).isEqualTo(player);
    }
}
