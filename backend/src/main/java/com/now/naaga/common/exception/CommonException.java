package com.now.naaga.common.exception;

public class CommonException extends BaseException {

    private final CommonExceptionType commonExceptionType;

    public CommonException(final CommonExceptionType commonExceptionType) {
        this.commonExceptionType = commonExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return commonExceptionType;
    }
}
