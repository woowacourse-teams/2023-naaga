package com.now.naaga.game.exception;

import com.now.naaga.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum GameExceptionType implements BaseExceptionType {

    INACCESSIBLE_AUTHENTICATION(
            403,
            HttpStatus.FORBIDDEN,
            "게임에 접근할 수 있는 권한이 없습니다."
    ),

    NOT_EXIST(
            404,
            HttpStatus.NOT_FOUND,
            "게임이 존재하지 않습니다."
    ),

    ALREADY_IN_PROGRESS(
            405,
            HttpStatus.BAD_REQUEST,
            "게임이 진행 중 입니다."
    ),

    CAN_NOT_FIND_PLACE(
            406,
            HttpStatus.BAD_REQUEST,
            "배정 할 목적지가 존재하지 않습니다."
    ),

    NOT_ARRIVED(
            415,
            HttpStatus.BAD_REQUEST,
            "목적지에 도착하지 않았습니다."
    ),

    ALREADY_DONE(
            416,
            HttpStatus.BAD_REQUEST,
            "이미 종료된 게임입니다."
    ),

    NOT_DONE(
            417,
            HttpStatus.BAD_REQUEST,
            "아직 종료되지 않은 게임입니다."
    ),

    HINT_NOT_EXIST_IN_GAME(
            454,
            HttpStatus.NOT_FOUND,
            "게임에서 해당 힌트가 존재하지 않습니다."
    ),

    HINTS_EXHAUSTED(
            455,
            HttpStatus.BAD_REQUEST,
            "사용할 수 있는 힌트를 모두 소진했습니다."
    ),

    NOT_REMAIN_ATTEMPTS(
            418,
            HttpStatus.BAD_REQUEST,
            "시도 횟수를 이미 다 사용한 게임입니다"
    ),

    NOT_EXIST_IN_PROGRESS(
            414,
            HttpStatus.NOT_FOUND,
            "진행 중인 게임이 존재하지 않습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    GameExceptionType(final int errorCode,
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
