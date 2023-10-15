package com.now.naaga.letter.exception;


import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class LetterException extends BaseException {

    private final LetterExceptionType letterExceptionType;

    public LetterException(final LetterExceptionType letterExceptionType) {
        this.letterExceptionType = letterExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return letterExceptionType;
    }
}
