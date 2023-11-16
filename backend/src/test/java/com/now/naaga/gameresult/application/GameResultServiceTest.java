package com.now.naaga.gameresult.application;

import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.gameresult.domain.ResultType.SUCCESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.now.naaga.common.ServiceTest;
import com.now.naaga.game.application.dto.CreateGameResultCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
class GameResultServiceTest extends ServiceTest {

    @Autowired
    private GameResultService gameResultService;

    @Test
    @Transactional
    void 사용자의_위치가_도착_범위_안이고_엔트타입이_도착이면_게임_결과를_성공으로_생성_후_저장하고_플레이어의_점수를_올린다() {
        //given
        final Player player = playerBuilder.init().build();
        final Score beforeScore = player.getTotalScore();
        final Game game = gameBuilder.init().player(player).gameStatus(DONE).endTime(LocalDateTime.now().plusMinutes(30)).build();
        final Position position = game.getPlace().getPosition();
        final CreateGameResultCommand createGameResultCommand = new CreateGameResultCommand(player, game, position, ARRIVED);

        //when
        gameResultService.createGameResult(createGameResultCommand);

        //then
        final GameResult gameResult = gameResultRepository.findByGameId(game.getId()).get(0);
        final Score totalScore = gameResult.getGame().getPlayer().getTotalScore();
        final Score expectedScore = beforeScore.plus(gameResult.getScore());
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(gameResult.getResultType()).isEqualTo(SUCCESS);
            softAssertions.assertThat(totalScore).isEqualTo(expectedScore);
        });
    }

    @Test
    void 종료되지_않은_게임의_게임결과를_만들려하면_예외를_발생한다() {
        //given
        final Player player = playerBuilder.init().build();
        final Game game = gameBuilder.init().player(player).gameStatus(IN_PROGRESS).build();
        final Position position = game.getPlace().getPosition();
        final CreateGameResultCommand createGameResultCommand = new CreateGameResultCommand(player, game, position, ARRIVED);

        //when & then
        assertThatThrownBy(() -> gameResultService.createGameResult(createGameResultCommand))
                .isInstanceOf(GameException.class);
    }
}
