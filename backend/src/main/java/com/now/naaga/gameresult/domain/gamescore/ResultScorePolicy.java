package com.now.naaga.gameresult.domain.gamescore;

import com.now.naaga.game.domain.Game;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.score.domain.Score;

public interface ResultScorePolicy {

    Score calculate(final Game game);
    
    boolean hasSameResultType(final ResultType resultType);
}
