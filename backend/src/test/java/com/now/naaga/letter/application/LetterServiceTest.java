package com.now.naaga.letter.application;

import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.now.naaga.common.fixture.PositionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@SpringBootTest
class LetterServiceTest {

    @Autowired
    private LetterService letterService;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    @Test
    void 플레이어주변_100m_내로의_쪽지만_모두_조회한다() {
        // given
        final Player registerPlayer = playerBuilder.init()
                .build();

        final Letter letter1 = letterBuilder.init()
                .registeredPlayer(registerPlayer)
                .build();

        final Letter letter2 = letterBuilder.init()
                .registeredPlayer(registerPlayer)
                .position(잠실역_교보문고_110미터_앞_좌표)
                .build();

        // when
        final List<Letter> actual = letterService.findNearByLetters(new FindNearByLetterCommand(잠실역_교보문고_좌표));

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.size()).isEqualTo(1);
            softAssertions.assertThat(actual.get(0).getId()).isEqualTo(letter1.getId());
        });
    }

    @Test
    void 플레이어주변_100m_내로의_쪽지가_없으면_빈리스트를_반환한다() {
        // given
        final Player registerPlayer = playerBuilder.init()
                .build();

        final Letter letter = letterBuilder.init()
                .registeredPlayer(registerPlayer)
                .position(잠실역_교보문고_110미터_앞_좌표)
                .build();

        // when
        final List<Letter> actual = letterService.findNearByLetters(new FindNearByLetterCommand(잠실_루터회관_정문_좌표));

        // then
        assertThat(actual).isEmpty();
    }
}
