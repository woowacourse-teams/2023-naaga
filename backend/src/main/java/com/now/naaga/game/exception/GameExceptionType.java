package com.now.naaga.game.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum GameExceptionType implements BaseExceptionType {

    ALREADY_IN_PROGRESS(
            400,
            HttpStatus.BAD_REQUEST,
            "이미 게임이 진행 중 입니다."
    ),

    NOT_EXIST(
            401,
            HttpStatus.NOT_FOUND,
            "게임이 존재하지 않습니다."
    ),

    INACCESSIBLE_AUTHENTICATION(
            402,
            HttpStatus.FORBIDDEN,
            "게임에 접근할 수 있는 권한이 없습니다."
    ),

    NOT_ARRIVED(
            403,
            HttpStatus.BAD_REQUEST,
            "목적지에 도착하지 않았습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    GameExceptionType(final int errorCode,
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
