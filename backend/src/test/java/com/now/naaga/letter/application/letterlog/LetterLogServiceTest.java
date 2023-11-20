package com.now.naaga.letter.application.letterlog;

import com.now.naaga.common.ServiceTest;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST_IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LetterLogServiceTest extends ServiceTest {

    @Autowired
    private LetterLogService letterLogService;

    @Test
    public void 읽은_쪽지_로그를_정상적으로_기록한다() throws ExecutionException, InterruptedException {
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
//        final CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
//            letterLogService.logReadLetter(player, letter);
//        });
//        completableFuture.get();

        letterLogService.logReadLetter(player, letter);

        // then
        final List<ReadLetterLog> actual = readLetterLogRepository.findAll();
        final long expected = actual.get(0).getLetter().getId();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(1);
            softAssertions.assertThat(expected).isEqualTo(letter.getId());
        });
    }

    @Test
    void 이미_읽은_쪽지의_경우_읽은_쪽지_로그를_기록하지_않는다() throws ExecutionException, InterruptedException {
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

        final ReadLetterLog readLetterLog = readLetterLogBuilder.init()
                .game(game)
                .letter(letter)
                .build();

        // when
        final CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            letterLogService.logReadLetter(player, letter);
        });
        completableFuture.get();

        // then
        final List<ReadLetterLog> actual = readLetterLogRepository.findAll();
        final long expected = actual.get(0).getLetter().getId();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(1);
            softAssertions.assertThat(expected).isEqualTo(letter.getId());
        });
    }

    @Test
    void 읽은쪽지로그에_데이터저장시_진행중인_게임이없으면_예외가_발생한다() {
        // given && when
        final Player player = playerBuilder.init()
                .build();

        final Place destination = placeBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();

        final Game game = gameBuilder.init()
                .place(destination)
                .player(player)
                .startPosition(잠실역_교보문고_좌표)
                .gameStatus(GameStatus.DONE)
                .build();

        final Player letterRegister = playerBuilder.init()
                .build();

        final Letter letter = letterBuilder.init()
                .registeredPlayer(letterRegister)
                .build();

        //then
        final GameException gameException = assertThrows(
                GameException.class, () -> letterLogService.logReadLetter(player, letter));
        assertThat(gameException.exceptionType()).isEqualTo(NOT_EXIST_IN_PROGRESS);
    }
}
