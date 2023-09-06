package com.now.naaga.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonExceptionType implements BaseExceptionType {

    INVALID_REQUEST_BODY(
            205,
            HttpStatus.BAD_REQUEST,
            "요청 바디 값이 잘못 되었습니다."
    ),

    INVALID_REQUEST_PARAMETERS(
            206,
            HttpStatus.BAD_REQUEST,
            "요청 파라미터 값이 잘못 되었습니다."
    ),

    FILE_SAVE_ERROR(
            215,
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
