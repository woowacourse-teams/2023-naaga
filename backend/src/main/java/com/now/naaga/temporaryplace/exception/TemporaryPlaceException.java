package com.now.naaga.temporaryplace.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class TemporaryPlaceException extends BaseException {

    private final TemporaryPlaceExceptionType temporaryPlaceExceptionType;

    public TemporaryPlaceException(final TemporaryPlaceExceptionType temporaryPlaceExceptionType) {
        this.temporaryPlaceExceptionType = temporaryPlaceExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return temporaryPlaceExceptionType;
    }
}
