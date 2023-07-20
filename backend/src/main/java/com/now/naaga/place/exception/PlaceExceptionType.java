package com.now.naaga.place.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PlaceExceptionType implements BaseExceptionType {

    PLACE_NOT_FOUND(
            300,
            HttpStatus.BAD_REQUEST,
            "배정 할 목적지가 존재하지 않습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    PlaceExceptionType(final int errorCode,
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
