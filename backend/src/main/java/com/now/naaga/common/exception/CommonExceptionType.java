package com.now.naaga.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonExceptionType implements BaseExceptionType {

    FILE_SAVE_ERROR(
            1001,
            HttpStatus.BAD_REQUEST,
            "파일 저장하다 문제가 발생했습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    CommonExceptionType(final int errorCode,
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
