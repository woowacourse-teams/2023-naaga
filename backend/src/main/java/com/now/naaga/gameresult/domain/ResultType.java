package com.now.naaga.gameresult.domain;

import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.gameresult.exception.GameResultException;
import com.now.naaga.gameresult.exception.GameResultExceptionType;
import com.now.naaga.place.domain.Position;

import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;

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
