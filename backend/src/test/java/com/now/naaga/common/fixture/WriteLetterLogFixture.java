package com.now.naaga.common.fixture;

import com.now.naaga.letter.domain.letterlog.WriteLetterLog;

import static com.now.naaga.common.fixture.GameFixture.GAME;
import static com.now.naaga.common.fixture.LetterFixture.LETTER;

public class WriteLetterLogFixture {

    public static WriteLetterLog WRITE_LETTER_LOG() {
        return new WriteLetterLog(GAME(), LETTER());
    }
}
