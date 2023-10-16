package com.now.naaga.letter.application;

import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.letter.exception.LetterExceptionType.NO_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@SpringBootTest
class LetterServiceTest {

    @Autowired
    private LetterService letterService;

    @Autowired
    private ReadLetterLogRepository readLetterLogRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    @Test
    void 쪽지를_단건조회_한다() {
        // given
        final Player player = playerBuilder.init()
                .build();

        final Place destination = placeBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();

        final Game game = gameBuilder.init()
                .place(destination)
                .player(player)
                .startPosition(잠실역_교보문고_좌표)
                .build();

        final Player letterRegister = playerBuilder.init()
                .build();

        final Letter letter = letterBuilder.init()
                .registeredPlayer(letterRegister)
                .build();

        // when
        final Letter actual = letterService.findLetter(new LetterReadCommand(player.getId(), letter.getId()));

        // then
        assertThat(actual.getId()).isEqualTo(letter.getId());
    }

    @Test
    void 쪽지가_존재하지_않으면_예외가_발생한다() {
        // given & when
        final Player player = playerBuilder.init()
                .build();

        final Place destination = placeBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();

        final Game game = gameBuilder.init()
                .place(destination)
                .player(player)
                .startPosition(잠실역_교보문고_좌표)
                .build();

        final Player letterRegister = playerBuilder.init()
                .build();

        final Letter letter = letterBuilder.init()
                .registeredPlayer(letterRegister)
                .build();

        // then
        final LetterException letterException = assertThrows(
                LetterException.class, () -> letterService.findLetter(new LetterReadCommand(player.getId(), letter.getId() + 1)));
        assertThat(letterException.exceptionType()).isEqualTo(NO_EXIST);
    }
}
