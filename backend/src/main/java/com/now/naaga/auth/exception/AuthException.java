package com.now.naaga.auth.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class AuthException extends BaseException {

    private final AuthExceptionType authExceptionType;

    public AuthException(final AuthExceptionType authExceptionType) {
        this.authExceptionType = authExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return authExceptionType;
    }
}
