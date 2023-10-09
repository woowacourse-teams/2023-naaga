package com.now.naaga.gameresult.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class GameResultException extends BaseException {

    private final GameResultExceptionType gameResultExceptionType;

    public GameResultException(final GameResultExceptionType gameResultExceptionType) {
        this.gameResultExceptionType = gameResultExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return gameResultExceptionType;
    }
}
