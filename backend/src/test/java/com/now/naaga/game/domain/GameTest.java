package com.now.naaga.game.domain;

import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.domain.EndType.GIVE_UP;
import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.domain.ResultType.FAIL;
import static com.now.naaga.game.domain.ResultType.SUCCESS;
import static com.now.naaga.game.exception.GameExceptionType.ALREADY_DONE;
import static com.now.naaga.game.exception.GameExceptionType.NOT_ARRIVED;
import static com.now.naaga.game.fixture.MemberFixture.MEMBER_CHAE;
import static com.now.naaga.game.fixture.PlaceFixture.잠실_루터회관;
import static com.now.naaga.game.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.game.fixture.PositionFixture.GS25_방이도곡점_좌표;
import static com.now.naaga.game.fixture.PositionFixture.던킨도너츠_올림픽공원점_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_근처_좌표;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.game.exception.GameException;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GameTest {

    private final Player player = PLAYER("chae", MEMBER_CHAE());
    ;
    private Game game;

    @Test
    void 게임을_포기한_경우_진행중인_게임은_실패로_종료한다() {
        // given
        Place destination = 잠실_루터회관(player);
        Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        Position currentPosition = GS25_방이도곡점_좌표;
        game = new Game(player, destination, startPosition);

        //when
        ResultType gameResult = game.endGame(GIVE_UP, currentPosition);

        // then
        assertThat(game.getGameStatus()).isEqualTo(DONE);
        assertThat(game.getRemainingAttempts()).isEqualTo(MAX_ATTEMPT_COUNT);
        assertThat(gameResult).isEqualTo(FAIL);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void 제한_횟수_내에_목적지에_도착한_경우_진행중인_게임은_성공으로_종료한다(int remainingAttempts) {
        // given
        Place destination = 잠실_루터회관(player);
        Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        Position currentPosition = 잠실_루터회관_정문_근처_좌표;
        game = new Game(IN_PROGRESS, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //when
        ResultType actual = game.endGame(ARRIVED, currentPosition);

        // then
        assertThat(game.getGameStatus()).isEqualTo(DONE);
        assertThat(game.getRemainingAttempts()).isEqualTo(remainingAttempts - 1);
        assertThat(actual).isEqualTo(SUCCESS);
    }

    @Test
    void 마지막_시도에_도착하지_못한_경우_진행중인_게임은_실패로_종료한다() {
        // given
        int remainingAttempts = 1;
        Place destination = 잠실_루터회관(player);
        Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        Position currentPosition = GS25_방이도곡점_좌표;
        game = new Game(IN_PROGRESS, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //when
        ResultType actual = game.endGame(ARRIVED, currentPosition);

        // then
        assertThat(game.getGameStatus()).isEqualTo(DONE);
        assertThat(game.getRemainingAttempts()).isEqualTo(remainingAttempts - 1);
        assertThat(actual).isEqualTo(FAIL);
    }

    //예외 발생 사례
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void 게임이_종료된_경우_예외가_발생한다(int remainingAttempts) {
        // given
        Place destination = 잠실_루터회관(player);
        Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        Position currentPosition = 잠실_루터회관_정문_근처_좌표;
        game = new Game(DONE, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //then
        GameException gameException = assertThrows(GameException.class, () -> game.endGame(ARRIVED, currentPosition));
        assertThat(gameException.exceptionType()).isEqualTo(ALREADY_DONE);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void 마지막_시도가_아닌_제한_횟수_내에_목적지에_도착하지_못한_경우_예외가_발생한다(int remainingAttempts) {
        // given
        Place destination = 잠실_루터회관(player);
        Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        Position currentPosition = GS25_방이도곡점_좌표;
        game = new Game(IN_PROGRESS, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.now(), null);

        //then
        GameException gameException = assertThrows(GameException.class, () -> game.endGame(ARRIVED, currentPosition));
        assertThat(gameException.exceptionType()).isEqualTo(NOT_ARRIVED);
    }
}
