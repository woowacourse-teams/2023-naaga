package com.now.naaga.game.domain;

import static com.now.naaga.common.fixture.PlaceFixture.PLACE;
import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.던킨도너츠_올림픽공원점_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.gameresult.domain.gamescore.ResultScorePolicy;
import com.now.naaga.gameresult.domain.gamescore.SuccessResultScorePolicy;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResultScorePolicyTest {

    private final Player player = PLAYER();
    private final ResultScorePolicy gameScorer = new SuccessResultScorePolicy();

    @Test
    void 다른_조건이_같고_거리가_멀수록_점수가_높다() {
        //given
        List<Hint> hints = List.of(new Hint(), new Hint());
        LocalDateTime startTime = LocalDateTime.of(2023, 7, 31, 12, 00, 30);
        LocalDateTime endTime = LocalDateTime.of(2023, 7, 31, 14, 00, 30);
        Game gameHasDestinationInFurtherArea = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, 3, hints, startTime, endTime);
        Game gameHasDestinationInNearArea = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 잠실역_교보문고_좌표, 3, hints, startTime, endTime);

        //when
        Score furtherAreaScore = gameScorer.calculate(gameHasDestinationInFurtherArea);
        Score nearerAreaScore = gameScorer.calculate(gameHasDestinationInNearArea);

        //then
        assertThat(furtherAreaScore.isHigherThan(nearerAreaScore))
                .isTrue();
    }

    @Test
    void 다른_조건이_같고_소요_시간이_짧을수록_점수가_높다() {
        //given
        List<Hint> hints = List.of(new Hint(), new Hint());
        LocalDateTime startTime = LocalDateTime.of(2023, 7, 31, 12, 00, 30);
        LocalDateTime slowerEndTime = LocalDateTime.of(2023, 7, 31, 15, 00, 30);
        LocalDateTime fasterEndTime = LocalDateTime.of(2023, 7, 31, 13, 00, 30);
        Game slowerGame = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, 3, hints, startTime, slowerEndTime);
        Game fasterGame = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, 3, hints, startTime, fasterEndTime);

        //when
        Score slowerGameScore = gameScorer.calculate(slowerGame);
        Score fasterGameScore = gameScorer.calculate(fasterGame);
        //then
        assertThat(fasterGameScore.isHigherThan(slowerGameScore))
                .isTrue();
    }

    @Test
    void 다른_조건이_같고_힌트_사용_개수가_적을수록_점수가_높다() {
        //given
        List<Hint> threeHints = List.of(new Hint(), new Hint(), new Hint());
        List<Hint> oneHints = List.of(new Hint());
        LocalDateTime startTime = LocalDateTime.of(2023, 7, 31, 12, 00, 30);
        LocalDateTime endTime = LocalDateTime.of(2023, 7, 31, 15, 00, 30);
        Game threeHintsGame = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, 3, threeHints, startTime, endTime);
        Game oneHintsGame = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, 3, oneHints, startTime, endTime);

        //when
        Score threeHintsGameScore = gameScorer.calculate(threeHintsGame);
        Score oneHintsGameScore = gameScorer.calculate(oneHintsGame);
        //then
        assertThat(oneHintsGameScore.isHigherThan(threeHintsGameScore))
                .isTrue();
    }

    @Test
    void 다른_조건이_같고_잔여_시도_횟수가_많을수록_점수가_높다() {
        //given
        int threeRemainingAttempts = 3;
        int oneRemainingAttempts = 1;
        List<Hint> hints = List.of(new Hint(), new Hint());
        LocalDateTime startTime = LocalDateTime.of(2023, 7, 31, 12, 00, 30);
        LocalDateTime endTime = LocalDateTime.of(2023, 7, 31, 15, 00, 30);
        Game threeRemainingAttemptsGame = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, threeRemainingAttempts, hints, startTime, endTime);
        Game oneRemainingAttemptsGame = new Game(DONE, player, PLACE(잠실_루터회관_정문_좌표, player), 던킨도너츠_올림픽공원점_좌표, oneRemainingAttempts, hints, startTime, endTime);

        //when
        Score threeRemainingAttemptsGameScore = gameScorer.calculate(threeRemainingAttemptsGame);
        Score oneRemainingAttemptsGameScore = gameScorer.calculate(oneRemainingAttemptsGame);
        //then
        assertThat(threeRemainingAttemptsGameScore.isHigherThan(oneRemainingAttemptsGameScore))
                .isTrue();
    }
}
