package com.now.naaga.gameresult.domain.gamescore;

import static com.now.naaga.gameresult.domain.ResultType.FAIL;

import com.now.naaga.game.domain.Game;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.score.domain.Score;

public class FailResultScorePolicy implements ResultScorePolicy {
    
    @Override
    public Score calculate(final Game game) {
        return new Score(0);
    }
    
    @Override
    public boolean hasSameResultType(final ResultType resultType) {
        return resultType == FAIL;
    }
}
