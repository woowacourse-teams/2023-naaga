package com.now.naaga.game.application;

import static com.now.naaga.game.domain.Game.MAXIMUM_HINT_USE_COUNT;
import static com.now.naaga.game.fixture.GameFixture.SEOUL_TO_JEJU_GAME;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.game.application.dto.CreateHintCommand;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.game.repository.GameRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
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
class HintServiceTest {

    @Autowired
    private HintService hintService;

    @Autowired
    private GameRepository gameRepository;

    private Game game;

    @BeforeEach
    void setUp() {
        game = gameRepository.save(SEOUL_TO_JEJU_GAME());
    }

    @Test
    void 힌트를_생성한다() {
        // given
        final CreateHintCommand createHintCommand = new CreateHintCommand(
                game.getId(),
                game.getPlayer().getId(),
                SEOUL_POSITION());

        // when
        final Hint actual = hintService.createHint(createHintCommand);

        // then
        final Hint expected = new Hint(
                SEOUL_POSITION(),
                Direction.SOUTH,
                SEOUL_TO_JEJU_GAME());
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);
    }

    @Test
    void 사용_가능한_힌트_개수를_모두_소진했을때_힌트를_생성하면_예외가_발생한다() {
        // given
        final CreateHintCommand createHintCommand = new CreateHintCommand(
                game.getId(),
                game.getPlayer().getId(),
                SEOUL_POSITION());

        // when
        for (int i = 0; i < MAXIMUM_HINT_USE_COUNT; i++) {
            hintService.createHint(createHintCommand);
        }

        // then
        final BaseExceptionType baseExceptionType = assertThrows(GameException.class, () ->
                hintService.createHint(createHintCommand)
        ).exceptionType();
        assertThat(baseExceptionType).isEqualTo(GameExceptionType.HINTS_EXHAUSTED);
    }
}