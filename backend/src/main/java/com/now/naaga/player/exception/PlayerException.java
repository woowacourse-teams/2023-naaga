package com.now.naaga.player.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class PlayerException extends BaseException {

    private final PlayerExceptionType playerExceptionType;

    public PlayerException(final PlayerExceptionType playerExceptionType) {
        this.playerExceptionType = playerExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return playerExceptionType;
    }
}
