package com.now.naaga.game.domain;

import com.now.naaga.score.domain.Score;

public interface ScorePolicy {
    
    Score calculate(final Game game);
    
}
