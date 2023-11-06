package com.now.naaga.temporaryplace.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum TemporaryPlaceExceptionType implements BaseExceptionType {
    
    NOT_EXIST(
            604,
            HttpStatus.NOT_FOUND,
            "검수 할 장소가 존재하지 않습니다"
    ),
    ;
    
    private final int errorCode;
    
    private final HttpStatus httpStatus;
    
    private final String errorMessage;
    
    TemporaryPlaceExceptionType(final int errorCode,
                                final HttpStatus httpStatus,
                                final String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
    
    @Override
    public int errorCode() {
        return errorCode();
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
