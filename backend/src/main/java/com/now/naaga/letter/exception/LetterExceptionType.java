package com.now.naaga.letter.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum LetterExceptionType implements BaseExceptionType {

    INACCESSIBLE_AUTHENTICATION(
            603,
            HttpStatus.FORBIDDEN,
            "쪽지에 접근할 수 있는 권한이 없습니다."
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
