package com.now.naaga.game.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class GameException extends BaseException {

    private final GameExceptionType gameExceptionType;

    public GameException(final GameExceptionType gameExceptionType) {
        this.gameExceptionType = gameExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return gameExceptionType;
    }
}
