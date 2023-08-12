package com.now.naaga.game.domain.gamescore;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.score.domain.Score;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GameScoreCalculator {
    
    private final List<GameScorePolicy> scorePolicies;
    
    public GameScoreCalculator(final List<GameScorePolicy> scorePolicies) {
        this.scorePolicies = scorePolicies;
    }
    
    public Score calculate(final Game game,
                           final ResultType resultType) {
        return scorePolicies.stream()
                .filter(gameScorePolicy -> gameScorePolicy.hasSameResultType(resultType))
                .map(gameScorePolicy -> gameScorePolicy.calculate(game))
                .findAny()
                .orElseGet(() -> new Score(0));
    }
}
