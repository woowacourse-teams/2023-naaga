package com.now.naaga.member.exception;

import com.now.naaga.common.exception.BaseException;
import com.now.naaga.common.exception.BaseExceptionType;

public class MemberException extends BaseException {

    private final MemberExceptionType memberExceptionType;

    public MemberException(final MemberExceptionType memberExceptionType) {
        this.memberExceptionType = memberExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return memberExceptionType;
    }
}
