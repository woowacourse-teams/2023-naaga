package com.now.naaga.game.domain.gamescore;

import com.now.naaga.common.exception.InternalException;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.score.domain.Score;

import java.util.List;

import static com.now.naaga.common.exception.InternalExceptionType.FAIL_ESTABLISH_GAME_SCORE_POLICY;

public class GameScoreCalculator {
    
    private final List<GameScorePolicy> scorePolicies;

    public GameScoreCalculator(final List<GameScorePolicy> scorePolicies) {
        this.scorePolicies = scorePolicies;
    }

    public Score calculate(final Game game,
                           final ResultType resultType) {
        final GameScorePolicy gameScorePolicy = scorePolicies.stream()
                .filter(policy -> policy.hasSameResultType(resultType))
                .findAny()
                .orElseThrow(() -> new InternalException(FAIL_ESTABLISH_GAME_SCORE_POLICY));
        return gameScorePolicy.calculate(game);
    }
}
