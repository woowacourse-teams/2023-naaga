package com.now.naaga.game.application;

import static com.now.naaga.game.domain.Game.MAX_HINT_COUNT;
import static com.now.naaga.game.exception.GameExceptionType.HINTS_EXHAUSTED;
import static com.now.naaga.game.exception.GameExceptionType.HINT_NOT_EXIST_IN_GAME;
import static com.now.naaga.game.fixture.GameFixture.SEOUL_TO_JEJU_GAME;
import static com.now.naaga.place.fixture.PlaceFixture.JEJU_PLACE;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.game.application.dto.CreateHintCommand;
import com.now.naaga.game.application.dto.FindHintByIdCommand;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.persistence.repository.PlaceRepository;
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

    @Autowired
    private PlaceRepository placeRepository;

    private Game game;

    @BeforeEach
    void setUp() {
        final Place place = placeRepository.save(JEJU_PLACE());
        game = gameRepository.save(SEOUL_TO_JEJU_GAME(place));
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
        for (int i = 0; i < MAX_HINT_COUNT; i++) {
            hintService.createHint(createHintCommand);
        }

        // then
        final BaseExceptionType baseExceptionType = assertThrows(GameException.class, () ->
                hintService.createHint(createHintCommand)
        ).exceptionType();
        assertThat(baseExceptionType).isEqualTo(HINTS_EXHAUSTED);
    }

    @Test
    void 힌트_id를_통해_힌트를_조회한다() {
        // given
        final CreateHintCommand createHintCommand = new CreateHintCommand(
                game.getId(),
                game.getPlayer().getId(),
                SEOUL_POSITION());

        final Hint hint = hintService.createHint(createHintCommand);

        final FindHintByIdCommand findHintByIdCommand = new FindHintByIdCommand(
                hint.getId(),
                game.getId(),
                game.getPlayer().getId());

        // when
        final Hint actual = hintService.findHintById(findHintByIdCommand);

        // then
        assertThat(actual).isEqualTo(hint);
    }

    @Test
    void 해당_게임에_없는_힌트를_조회하면_예외가_발생한다() {
        // given
        final CreateHintCommand createHintCommand = new CreateHintCommand(
                game.getId(),
                game.getPlayer().getId(),
                SEOUL_POSITION());

        final Hint hint = hintService.createHint(createHintCommand);

        final FindHintByIdCommand findHintByIdCommand = new FindHintByIdCommand(
                hint.getId() + 1L,
                game.getId(),
                game.getPlayer().getId());

        // when
        final BaseExceptionType baseExceptionType = assertThrows(GameException.class, () ->
                hintService.findHintById(findHintByIdCommand)
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(HINT_NOT_EXIST_IN_GAME);
    }
}