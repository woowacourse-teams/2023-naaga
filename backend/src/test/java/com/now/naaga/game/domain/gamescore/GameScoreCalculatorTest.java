package com.now.naaga.game.domain.gamescore;

import static com.now.naaga.common.fixture.PlaceFixture.PLACE;
import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.서울_좌표;
import static com.now.naaga.gameresult.domain.ResultType.FAIL;
import static com.now.naaga.gameresult.domain.ResultType.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.gameresult.domain.gamescore.GameScoreCalculator;
import com.now.naaga.score.domain.Score;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class GameScoreCalculatorTest {

    @Autowired
    GameScoreCalculator gameScoreCalculator;

    @Test
    void 게임결과가_성공일_때_점수를_얻는다() {
        //given
        ResultType resultType = SUCCESS;
        Game game = new Game(GameStatus.DONE, PLAYER(), PLACE(), 서울_좌표, 3,
                             Collections.emptyList(),
                             LocalDateTime.now().minusHours(1), LocalDateTime.now());

        //when
        final Score actual = gameScoreCalculator.calculate(game, resultType);

        //then
        assertThat(actual.getValue()).isGreaterThan(0);
    }

    @Test
    void 게임결과가_실패일_때_0점을_얻는다() {
        //given
        ResultType resultType = FAIL;
        Game game = new Game(GameStatus.DONE, PLAYER(), PLACE(), 서울_좌표, 3,
                             Collections.emptyList(),
                             LocalDateTime.now().minusHours(1), LocalDateTime.now());

        //when
        final Score actual = gameScoreCalculator.calculate(game, resultType);

        //then
        assertThat(actual.getValue()).isZero();
    }
}
