package com.now.naaga.gameresult.application;

import com.now.naaga.game.application.dto.CreateGameResultCommand;
import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameFinishService;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.gameresult.domain.gamescore.ResultScoreCalculator;
import com.now.naaga.gameresult.persistence.GameResultRepository;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.application.dto.AddScoreCommand;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class GameResultService implements GameFinishService {

    private final GameResultRepository gameResultRepository;

    private final PlayerService playerService;

    private final ResultScoreCalculator resultScoreCalculator;

    public GameResultService(final GameResultRepository gameResultRepository,
                             final PlayerService playerService,
                             final ResultScoreCalculator resultScoreCalculator) {
        this.gameResultRepository = gameResultRepository;
        this.playerService = playerService;
        this.resultScoreCalculator = resultScoreCalculator;
    }

    @Override
    public void createGameResult(final CreateGameResultCommand createGameResultCommand) {
        // TODO: 8/31/23 Command를 ID로 바꾸면 gameService를 조회(onwer 검증까지)하는 메서드를 호출해서 Game을 가져와야 함 -> 이런 과정이 매우 많은데 하나의 메서드로 빼놓는 것도 좋을듯
        final Player player = createGameResultCommand.player();
        final Game game = createGameResultCommand.game();
        game.validateOwner(player);

        final EndType endType = createGameResultCommand.endType();
        final Position position = createGameResultCommand.position();

        final ResultType resultType = ResultType.decide(game, endType, position);
        final Score score = resultScoreCalculator.calculate(game, resultType);

        final AddScoreCommand addScoreCommand = new AddScoreCommand(player.getId(), score);
        playerService.addScore(addScoreCommand);

        final GameResult gameResult = new GameResult(resultType, score, game);
        gameResultRepository.save(gameResult);
    }
}
