package com.now.naaga.common.fixture;

import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;

import com.now.naaga.letter.domain.Letter;

public class LetterFixture {

    public static final String MESSAGE = "안녕하세요. 나아가 개발자들 입니다.";

    public static Letter LETTER() {
        return new Letter(PLAYER(), 잠실_루터회관_정문_좌표, MESSAGE);
    }
}
