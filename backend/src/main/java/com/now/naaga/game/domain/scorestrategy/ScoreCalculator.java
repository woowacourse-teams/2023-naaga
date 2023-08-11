package com.now.naaga.game.domain.scorestrategy;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.score.domain.Score;
import java.time.Period;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ScoreCalculator {
    
    private final List<ScorePolicy> scorePolicies;
    
    public ScoreCalculator(final List<ScorePolicy> scorePolicies) {
        this.scorePolicies = scorePolicies;
    }
    
    public Score calculate(final Game game,
                           final ResultType resultType) {
        return scorePolicies.stream()
                .filter(scorePolicy -> scorePolicy.hasSameResultType(resultType))
                .map(scorePolicy -> scorePolicy.calculate(game))
                .findAny()
                .orElseGet(() -> new Score(0));
    }
}
