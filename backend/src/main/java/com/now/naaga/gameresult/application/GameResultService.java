package com.now.naaga.gameresult.application;

import com.now.naaga.game.application.dto.CreateGameResultCommand;
import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameFinishService;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.gameresult.domain.gamescore.GameScoreCalculator;
import com.now.naaga.gameresult.persistence.GameResultRepository;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameResultService implements GameFinishService {

    private final GameResultRepository gameResultRepository;

    private final GameScoreCalculator gameScoreCalculator;

    public GameResultService(final GameResultRepository gameResultRepository,
                             final GameScoreCalculator gameScoreCalculator) {
        this.gameResultRepository = gameResultRepository;
        this.gameScoreCalculator = gameScoreCalculator;
    }

    @Override
    @Transactional
    public void createGameResult(final CreateGameResultCommand createGameResultCommand) {
        final Player player = createGameResultCommand.player();
        final Game game = createGameResultCommand.game();
        final EndType endType = createGameResultCommand.endType();
        final Position position = createGameResultCommand.position();

        final ResultType resultType = ResultType.decide(game, endType, position);
        final Score score = gameScoreCalculator.calculate(game, resultType);

        player.addScore(score);

        final GameResult gameResult = new GameResult(resultType, score, game);
        gameResultRepository.save(gameResult);
    }
}
