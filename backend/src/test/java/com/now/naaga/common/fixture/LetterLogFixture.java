package com.now.naaga.common.fixture;

import com.now.naaga.letterlog.domain.LetterLog;
import com.now.naaga.letterlog.domain.LetterLogType;

import static com.now.naaga.common.fixture.GameFixture.GAME;
import static com.now.naaga.common.fixture.LetterFixture.LETTER;

public class LetterLogFixture {

    public static LetterLog LETTER_LOG_READ() {
        return new LetterLog(GAME(), LETTER(), LetterLogType.READ);
    }

    public static LetterLog LETTER_LOG_WRITE() {
        return new LetterLog(GAME(), LETTER(), LetterLogType.WRITE);
    }
}
