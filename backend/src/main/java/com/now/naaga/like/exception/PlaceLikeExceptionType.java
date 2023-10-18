package com.now.naaga.like.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;

public enum PlaceLikeExceptionType implements BaseExceptionType {

    INACCESSIBLE_AUTHENTICATION(
            703,
            HttpStatus.FORBIDDEN,
            "접근 권한이 없는 좋아요/싫어요입니다."
    ),

    NOT_EXIST(
            704,
            HttpStatus.NOT_FOUND,
            "좋아요/싫어요가 존재하지 않습니다."
    ),

    ALREADY_APPLIED_TYPE(
            705,
            HttpStatus.BAD_REQUEST,
            "이미 등록된 좋아요/싫어요 입니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    PlaceLikeExceptionType(final int errorCode,
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
