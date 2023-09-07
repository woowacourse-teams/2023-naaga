package com.now.naaga.game.domain;

import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameNotFinishedException;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.now.naaga.common.fixture.PlaceFixture.PLACE;
import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.*;
import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.domain.EndType.GIVE_UP;
import static com.now.naaga.game.domain.Game.MAX_HINT_COUNT;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GameTest {

    private final Player player = PLAYER();

    @Test
    void 게임을_포기한_경우_진행중인_게임은_종료한다() {
        // given
        final Place destination = PLACE(잠실_루터회관_정문_좌표, player);
        final Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        final Position currentPosition = GS25_방이도곡점_좌표;
        final Game game = new Game(player, destination, startPosition);

        //when
        game.endGame(currentPosition, GIVE_UP);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(game.getGameStatus()).isEqualTo(DONE);
            softAssertions.assertThat(game.getRemainingAttempts()).isEqualTo(MAX_HINT_COUNT);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void 제한_횟수_내에_목적지에_도착한_경우_진행중인_게임은_성공으로_종료한다() {
        // given
        final Place destination = PLACE(잠실_루터회관_정문_좌표, player);
        final Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        final Position currentPosition = 잠실_루터회관_정문_근처_좌표;
        final Game game = new Game(player, destination, startPosition);

        //when
        game.endGame(currentPosition, ARRIVED);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(game.getGameStatus()).isEqualTo(DONE);
            softAssertions.assertThat(game.getRemainingAttempts()).isEqualTo(MAX_HINT_COUNT - 1);
        });
    }

    @Test
    void 마지막_시도에_도착하지_못한_경우_진행중인_게임은_실패로_종료한다() {
        // given
        final int remainingAttempts = 1;
        final Place destination = PLACE(잠실_루터회관_정문_좌표, player);
        final Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        final Position currentPosition = GS25_방이도곡점_좌표;
        final Game game = new Game(IN_PROGRESS, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //when
        game.endGame(currentPosition, ARRIVED);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(game.getGameStatus()).isEqualTo(DONE);
            softAssertions.assertThat(game.getRemainingAttempts()).isEqualTo(MAX_HINT_COUNT - 1);
        });
    }

    //예외 발생 사례
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void 게임이_종료된_경우_예외가_발생한다(int remainingAttempts) {
        // given
        final Place destination = PLACE(잠실_루터회관_정문_좌표, player);
        final Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        final Position currentPosition = 잠실_루터회관_정문_근처_좌표;
        final Game game = new Game(DONE, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //when & then
        assertThatThrownBy(() -> game.endGame(currentPosition, ARRIVED)).isInstanceOf(GameException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void 마지막_시도가_아닌_제한_횟수_내에_목적지에_도착하지_못한_경우_예외가_발생한다(int remainingAttempts) {
        // given
        final Place destination = PLACE(잠실_루터회관_정문_좌표, player);
        final Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        final Position currentPosition = GS25_방이도곡점_좌표;
        final Game game = new Game(IN_PROGRESS, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //when & then
        assertThatThrownBy(() -> game.endGame(currentPosition, ARRIVED)).isInstanceOf(GameNotFinishedException.class);
    }
}
