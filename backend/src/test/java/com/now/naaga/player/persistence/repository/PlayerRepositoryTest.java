package com.now.naaga.player.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@SpringBootTest
class PlayerRepositoryTest {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

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
