package com.now.naaga.common.fixture;

import static com.now.naaga.common.fixture.GameFixture.GAME;
import static com.now.naaga.common.fixture.LetterFixture.LETTER;

import com.now.naaga.letter.domain.letterlog.WriteLetterLog;

public class WriteLetterLogFixture {

    public static WriteLetterLog WRITE_LETTER_LOG() {
        return new WriteLetterLog(GAME(), LETTER());
    }
}
