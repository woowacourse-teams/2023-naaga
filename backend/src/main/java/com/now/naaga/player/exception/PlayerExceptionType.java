package com.now.naaga.player.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PlayerExceptionType implements BaseExceptionType {

    PLAYER_NOT_FOUND(
            600,
            HttpStatus.BAD_REQUEST,
            "플레이어가 존재하지 않습니다."
    ),
    INVALID_SORTING_REQUEST(
            601,
            HttpStatus.BAD_REQUEST,
            "잘못된 정렬 요청입니다."
    );

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    PlayerExceptionType(final int errorCode,
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
