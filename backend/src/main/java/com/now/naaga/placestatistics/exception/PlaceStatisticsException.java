package com.now.naaga.placestatistics.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class PlaceStatisticsException extends BaseException {

    private final PlaceStatisticsExceptionType placeStatisticsExceptionType;

    public PlaceStatisticsException(final PlaceStatisticsExceptionType placeStatisticsExceptionType) {
        this.placeStatisticsExceptionType = placeStatisticsExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return placeStatisticsExceptionType;
    }
}
