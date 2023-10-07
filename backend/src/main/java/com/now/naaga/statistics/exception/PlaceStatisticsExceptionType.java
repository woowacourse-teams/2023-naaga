package com.now.naaga.statistics.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PlaceStatisticsExceptionType implements BaseExceptionType {

    NOT_FOUND(
            804,
            HttpStatus.NOT_FOUND,
            "해당 장소 통계는 존재하지 않습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    PlaceStatisticsExceptionType(final int errorCode,
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
