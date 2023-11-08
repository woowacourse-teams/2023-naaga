package com.now.naaga.letter.domain.letterlog;

import com.now.naaga.letter.exception.LetterException;

import java.util.Arrays;

import static com.now.naaga.letter.exception.LetterExceptionType.INVALID_LOG_TYPE;

public enum LetterLogType {

    READ,
    WRITE,
    ;

    public static LetterLogType from(final String logType) {
        return Arrays.stream(values())
                .filter(enumValue -> enumValue.name().equalsIgnoreCase(logType))
                .findFirst()
                .orElseThrow(() -> new LetterException(INVALID_LOG_TYPE));
    }

    public boolean isWrite() {
        return this == WRITE;
    }
}
