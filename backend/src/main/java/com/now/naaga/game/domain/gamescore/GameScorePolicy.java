package com.now.naaga.game.domain.gamescore;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.score.domain.Score;

public interface GameScorePolicy {

    Score calculate(final Game game);
    
    boolean hasSameResultType(final ResultType resultType);
}
