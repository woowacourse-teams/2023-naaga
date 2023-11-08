package com.now.naaga.letter.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum LetterExceptionType implements BaseExceptionType {

    NO_EXIST(
            604,
            HttpStatus.NOT_FOUND,
            "해당 쪽지가 존재하지 않습니다."
    ),
    INVALID_LOG_TYPE(
            605,
            HttpStatus.NOT_FOUND,
            "로그 타입이 아닙니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    LetterExceptionType(final int errorCode,
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
