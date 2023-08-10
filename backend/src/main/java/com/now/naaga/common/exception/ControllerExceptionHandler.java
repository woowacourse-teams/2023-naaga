package com.now.naaga.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleBaseException(final BaseException e) {
        final BaseExceptionType baseExceptionType = e.exceptionType();
        final ExceptionResponse exceptionResponse = new ExceptionResponse(baseExceptionType.errorCode(), baseExceptionType.errorMessage());
        log.warn("error = {}", exceptionResponse);
        return ResponseEntity.status(baseExceptionType.httpStatus()).body(exceptionResponse);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<ExceptionResponse> handleTypeMismatchException(final Exception e) {
        final CommonExceptionType commonExceptionType = CommonExceptionType.INVALID_REQUEST_BODY;
        final ExceptionResponse exceptionResponse = new ExceptionResponse(commonExceptionType.errorCode(), commonExceptionType.errorMessage());
        log.warn("error = {}", exceptionResponse);
        return ResponseEntity.status(commonExceptionType.httpStatus()).body(exceptionResponse);
    }
}
