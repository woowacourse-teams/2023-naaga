package com.now.naaga.common.fixture;

import static com.now.naaga.common.fixture.GameFixture.GAME;
import static com.now.naaga.common.fixture.LetterFixture.LETTER;

import com.now.naaga.letter.domain.letterlog.ReadLetterLog;

public class ReadLetterLogFixture {

    public static ReadLetterLog READ_LETTER_LOG() {
        return new ReadLetterLog(GAME(), LETTER());
    }
}
