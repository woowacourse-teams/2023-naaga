package com.now.naaga.statistics.application;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.statistics.exception.PlaceStatisticsExceptionType;

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
