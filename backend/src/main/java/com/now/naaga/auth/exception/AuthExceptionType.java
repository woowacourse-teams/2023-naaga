package com.now.naaga.auth.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionType implements BaseExceptionType {

    PASSWORD_MISMATCH(
            100,
            HttpStatus.UNAUTHORIZED,
            "비밀번호가 일치하지 않습니다."
    ),

    NOT_EXIST_HEADER(
            101,
            HttpStatus.UNAUTHORIZED,
            "헤더 정보가 존재하지 않습니다."
    ),

    INVALID_HEADER(
            102,
            HttpStatus.UNAUTHORIZED,
            "헤더 정보가 유효하지 않습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    AuthExceptionType(final int errorCode,
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
