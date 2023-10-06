package com.now.naaga.gameresult.domain;

import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.place.domain.Position;

public enum ResultType {

    SUCCESS,
    FAIL,
    ;

    public static ResultType decide(final Game game,
                                    final EndType endType,
                                    final Position position) {
        game.validateDoneGame();

        if (endType == EndType.ARRIVED && game.getPlace().isCoordinateInsideBounds(position)) {
            return ResultType.SUCCESS;
        }
        return ResultType.FAIL;
    }
}
