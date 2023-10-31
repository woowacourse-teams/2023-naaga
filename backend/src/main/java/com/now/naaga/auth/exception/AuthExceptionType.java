package com.now.naaga.auth.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionType implements BaseExceptionType {

    NOT_EXIST_HEADER(
            101,
            HttpStatus.UNAUTHORIZED,
            "헤더 정보가 존재하지 않습니다."
    ),

    INVALID_KAKAO_INFO(
            101,
            HttpStatus.UNAUTHORIZED,
            "카카오에서 해당 정보를 가져올 수 없습니다."
    ),

    INVALID_TOKEN(
            101,
            HttpStatus.UNAUTHORIZED,
            "토큰 정보가 옳지 않습니다."
    ),

    INVALID_HEADER(
            101,
            HttpStatus.UNAUTHORIZED,
            "헤더 정보가 유효하지 않습니다."
    ),

    INVALID_TOKEN_ACCESS(
            101,
            HttpStatus.UNAUTHORIZED,
            "잘못된 토큰 접근 입니다."
    ),

    EXPIRED_TOKEN(
            102,
            HttpStatus.UNAUTHORIZED,
            "토큰이 만료 되었습니다."
    ),

    //todo : 고쳐줘
    INVALID_KAKAO_DELETE(101,
            HttpStatus.UNAUTHORIZED,
            "카카오에서 정보를 삭제할 수 없습니다."),

    INVALID_MANAGER(103,
            HttpStatus.UNAUTHORIZED,
            "관리자 정보가 옳지 않습니다.")
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    AuthExceptionType(final int errorCode,
                      final HttpStatus httpStatus,
                      final String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int errorCode() {
        return errorCode;
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
