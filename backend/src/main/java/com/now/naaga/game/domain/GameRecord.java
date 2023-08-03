package com.now.naaga.game.domain;

import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;

import com.now.naaga.place.domain.Position;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class GameRecord {

    private GameResult gameResult;
    private Duration totalPlayTime;
    private int distance;
    private int hintUses;
    private int tryCount;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    public GameRecord(final GameResult gameResult,
                      final Duration totalPlayTime,
                      final int distance,
                      final int hintUses,
                      final int tryCount,
                      final LocalDateTime startTime,
                      final LocalDateTime finishTime) {
        this.gameResult = gameResult;
        this.totalPlayTime = totalPlayTime;
        this.distance = distance;
        this.hintUses = hintUses;
        this.tryCount = tryCount;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public static GameRecord from(final GameResult gameResult) {
        final Duration totalPlayTime = calculateTotalPlayTime(gameResult.getGame().getStartTime(), gameResult.getGame().getEndTime());
        final int distance = calculateDistance(gameResult.getGame().getStartPosition(), gameResult.getGame().getPlace().getPosition());
        final int hintUses = gameResult.getGame().getHints().size();
        final int tryCount = MAX_ATTEMPT_COUNT - gameResult.getGame().getRemainingAttempts();
        final LocalDateTime startTime = gameResult.getGame().getStartTime();
        final LocalDateTime finishTime = gameResult.getGame().getEndTime();
        return new GameRecord(gameResult, totalPlayTime, distance, hintUses, tryCount, startTime, finishTime);
    }

    private static Duration calculateTotalPlayTime(final LocalDateTime startDateTime,
                                                 final LocalDateTime endDateTime) {
        return Duration.between(startDateTime, endDateTime);
    }

    private static int calculateDistance(final Position startPosition,
                                         final Position destinationPosition) {
        return (int) startPosition.calculateDistance(destinationPosition);
    }

    public String durationToString(Duration duration){
        final long hours = duration.toHours();
        final long minutes = duration.toMinutesPart();
        final long seconds = duration.toSecondsPart();
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public Duration getTotalPlayTime() {
        return totalPlayTime;
    }

    public int getDistance() {
        return distance;
    }

    public int getHintUses() {
        return hintUses;
    }

    public int getTryCount() {
        return tryCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }
}
