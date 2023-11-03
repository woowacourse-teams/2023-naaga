package com.now.naaga.gameresult.application;

import com.now.naaga.game.application.GameFinishService;
import com.now.naaga.game.application.dto.CreateGameResultCommand;
import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.gameresult.domain.gamescore.ResultScoreCalculator;
import com.now.naaga.gameresult.repository.GameResultRepository;
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
