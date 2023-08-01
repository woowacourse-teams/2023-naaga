package com.now.naaga.place.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PlaceExceptionType implements BaseExceptionType {

    CAN_NOT_FIND_PLACE(
            500,
            HttpStatus.BAD_REQUEST,
            "배정 할 목적지가 존재하지 않습니다."
    ),
    NO_EXIST(
            504,
            HttpStatus.NOT_FOUND,
            "해당 장소가 존재하지 않습니다."
    ),
    INACCESSIBLE_AUTHENTICATION(
            503,
            HttpStatus.FORBIDDEN,
            "해당 장소에 대한 접근권한이 없습니다."
    )
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
