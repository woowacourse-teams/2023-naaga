package com.now.naaga.common.builder;

import static com.now.naaga.game.domain.GameStatus.DONE;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.score.domain.Score;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameResultBuilder {

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private GameBuilder gameBuilder;

    private ResultType resultType;

    private Score score;

    private Optional<Game> game;

    public GameResultBuilder init() {
        this.resultType = ResultType.SUCCESS;
        this.score = new Score(50);
        this.game = Optional.empty();
        return this;
    }

    public GameResultBuilder resultType(final ResultType resultType) {
        this.resultType = resultType;
        return this;
    }

    public GameResultBuilder score(final Score score) {
        this.score = score;
        return this;
    }

    public GameResultBuilder game(final Game game) {
        this.game = Optional.ofNullable(game);
        return this;
    }

    public GameResult build() {
        final Game persistedGame = game.orElseGet(this::getPersistedGame);
        final GameResult gameResult = new GameResult(resultType, score, persistedGame);
        return gameResultRepository.save(gameResult);
    }

    private Game getPersistedGame() {
        return gameBuilder.init()
                          .gameStatus(DONE)
                          .endTime(LocalDateTime.now().plusHours(1))
                          .build();
    }
}
