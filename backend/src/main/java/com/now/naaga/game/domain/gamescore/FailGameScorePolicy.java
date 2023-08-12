package com.now.naaga.game.domain.gamescore;

import static com.now.naaga.game.domain.ResultType.FAIL;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Component;

@Component
public class FailGameScorePolicy implements GameScorePolicy {
    
    private final static ResultType RESULT_TYPE = FAIL;
    
    @Override
    public Score calculate(final Game game) {
        return new Score(0);
    }
    
    @Override
    public boolean hasSameResultType(final ResultType resultType) {
        return resultType == RESULT_TYPE;
    }
    
}
