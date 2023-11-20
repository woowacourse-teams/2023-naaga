//package com.now.naaga.letter.application.letterlog;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.Method;
//
//@Service
//public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
//
//    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
//
//    @Override
//    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
//        System.out.println("예외 잡다.");
//        log.error("async return 타입이 void 인 경우 예외 처리");
//    }
//}
