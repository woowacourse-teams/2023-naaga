package com.now.naaga.common.exception;

import org.springframework.http.HttpStatus;

public enum InternalExceptionType implements BaseExceptionType {

    FAIL_ESTABLISH_GAME_SCORE_POLICY(
            10001,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "점수 계산 정책을 정하지 못했습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    InternalExceptionType(final int errorCode,
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
