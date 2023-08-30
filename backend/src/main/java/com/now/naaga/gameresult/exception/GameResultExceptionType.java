package com.now.naaga.gameresult.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum GameResultExceptionType implements BaseExceptionType {

    GAME_RESULT_NOT_EXIST(
            434,
            HttpStatus.NOT_FOUND,
            "해당게임의 게임결과가 존재하지 않습니다."
    ),

    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    GameResultExceptionType(final int errorCode,
                            final HttpStatus httpStatus,
                            final String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int errorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}
