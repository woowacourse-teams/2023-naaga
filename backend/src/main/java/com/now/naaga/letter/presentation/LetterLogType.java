package com.now.naaga.letter.presentation;

import com.now.naaga.common.exception.CommonException;

import java.util.Arrays;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;

public enum LetterLogType {

    READ,
    WRITE,
    ;

    public static LetterLogType from(final String logType) {
        return Arrays.stream(values())
                .filter(enumValue -> enumValue.name().equalsIgnoreCase(logType))
                .findFirst()
                .orElseThrow(() -> new CommonException(INVALID_REQUEST_PARAMETERS));
    }
}
