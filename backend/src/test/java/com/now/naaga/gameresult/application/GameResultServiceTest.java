package com.now.naaga.gameresult.application;

import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.GameResultBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.game.application.dto.CreateGameResultCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.exception.GameResultException;
import com.now.naaga.gameresult.repository.GameResultRepository;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.gameresult.domain.ResultType.SUCCESS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class GameResultServiceTest {

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private GameResultService gameResultService;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private GameResultBuilder gameResultBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Test
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
