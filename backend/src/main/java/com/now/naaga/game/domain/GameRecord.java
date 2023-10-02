package com.now.naaga.game.domain;

import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;

import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.place.domain.Position;
import java.time.Duration;
import java.time.LocalDateTime;

public record GameRecord(GameResult gameResult,
                         Duration totalPlayTime,
                         int distance,
                         int hintUses,
                         int tryCount,
                         LocalDateTime startTime,
                         LocalDateTime finishTime) {

    public static GameRecord from(final GameResult gameResult) {
        final Duration totalPlayTime = calculateTotalPlayTime(gameResult);
        final int distance = calculateDistance(gameResult);
        final int hintUses = gameResult.getGame().getHints().size();
        final int tryCount = MAX_ATTEMPT_COUNT - gameResult.getGame().getRemainingAttempts();
        final LocalDateTime startTime = gameResult.getGame().getStartTime();
        final LocalDateTime finishTime = gameResult.getGame().getEndTime();
        return new GameRecord(gameResult, totalPlayTime, distance, hintUses, tryCount, startTime, finishTime);
    }

    private static Duration calculateTotalPlayTime(final GameResult gameResult) {
        final LocalDateTime startDateTime = gameResult.getGame().getStartTime();
        final LocalDateTime endDateTime = gameResult.getGame().getEndTime();
        return Duration.between(startDateTime, endDateTime);
    }

    private static int calculateDistance(final GameResult gameResult) {
        if (gameResult.getResultType() == ResultType.FAIL) {
            return 0;
        }
        final Position startPosition = gameResult.getGame().getStartPosition();
        final Position destinationPosition = gameResult.getGame().getPlace().getPosition();
        return (int) startPosition.calculateDistance(destinationPosition);
    }

    public int durationToInteger(Duration duration) {
        return (int) duration.toMinutes();
    }
}
