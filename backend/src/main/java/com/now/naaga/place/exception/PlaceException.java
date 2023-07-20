package com.now.naaga.place.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class PlaceException extends BaseException {

    private final PlaceExceptionType placeExceptionType;

    public PlaceException(final PlaceExceptionType placeExceptionType) {
        this.placeExceptionType = placeExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return placeExceptionType;
    }
}
