package com.now.naaga.letter.application.letterlog;


import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        final String errorMessage = ((BaseException) throwable).exceptionType().errorMessage();
        final int errorCode = ((BaseException) throwable).exceptionType().errorCode();
        final ExceptionResponse exceptionResponse = new ExceptionResponse(errorCode, errorMessage);

        log.info("error = {}", exceptionResponse);
    }
}
