package com.now.naaga.game.domain;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static com.now.naaga.common.fixture.PlaceFixture.PLACE;
import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.던킨도너츠_올림픽공원점_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.ResultType.SUCCESS;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StatisticTest {

    @Test
    void 게임_리코드를_받아_게임_통계를_생성한다() {
        // given & when
        int remainingAttempts = 1;
        Player player = PLAYER();
        Place destination = PLACE(잠실_루터회관_정문_좌표, player);

        Position startPosition = 던킨도너츠_올림픽공원점_좌표;
        Game game1 = new Game(DONE, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.of(2023, Month.AUGUST, 12, 15, 30, 0), LocalDateTime.of(2023, Month.AUGUST, 12, 17, 30, 0));
        GameResult gameResult1 = new GameResult(SUCCESS, new Score(15), game1);
        GameRecord gameRecord1 = GameRecord.from(gameResult1);

        Game game2 = new Game(DONE, player, destination, startPosition, remainingAttempts, new ArrayList<>(), LocalDateTime.of(2023, Month.AUGUST, 12, 15, 30, 0), LocalDateTime.of(2023, Month.AUGUST, 12, 17, 30, 0));
        GameResult gameResult2 = new GameResult(SUCCESS, new Score(15), game2);
        GameRecord gameRecord2 = GameRecord.from(gameResult2);

        List<GameRecord> gameRecords = new ArrayList<>();
        gameRecords.add(gameRecord1);
        gameRecords.add(gameRecord2);
        Statistic expected = Statistic.of(gameRecords);

        // then
        assertSoftly(softly -> {
            softly.assertThat(expected.getGameCount()).isEqualTo(2);
            softly.assertThat(expected.getSuccessGameCount()).isEqualTo(2);
            softly.assertThat(expected.getFailGameCount()).isEqualTo(0);
            softly.assertThat(expected.getTotalDistance()).isEqualTo(1652);
            softly.assertThat(expected.getTotalPlayTime()).isEqualTo(Duration.ofHours(4));
            softly.assertThat(expected.getTotalUsedHintCount()).isEqualTo(0);
        });
    }
}
