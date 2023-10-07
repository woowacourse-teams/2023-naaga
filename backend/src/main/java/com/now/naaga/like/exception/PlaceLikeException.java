package com.now.naaga.like.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class PlaceLikeException extends BaseException {

    private final PlaceLikeExceptionType placeLikeExceptionType;

    public PlaceLikeException(final PlaceLikeExceptionType placeLikeExceptionType) {
        this.placeLikeExceptionType = placeLikeExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return placeLikeExceptionType;
    }
}
