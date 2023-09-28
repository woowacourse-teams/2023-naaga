package com.now.naaga.game.domain;

import java.time.Duration;
import java.util.List;

import static com.now.naaga.gameresult.domain.ResultType.FAIL;
import static com.now.naaga.gameresult.domain.ResultType.SUCCESS;

public class Statistic {

    private int gameCount;
    private int successGameCount;
    private int failGameCount;
    private int totalDistance;
    private Duration totalPlayTime;
    private int totalUsedHintCount;

    public Statistic(final int gameCount,
                     final int successGameCount,
                     final int failGameCount,
                     final int totalDistance,
                     final Duration totalPlayTime,
                     final int totalUsedHintCount) {
        this.gameCount = gameCount;
        this.successGameCount = successGameCount;
        this.failGameCount = failGameCount;
        this.totalDistance = totalDistance;
        this.totalPlayTime = totalPlayTime;
        this.totalUsedHintCount = totalUsedHintCount;
    }

    public static Statistic of(final List<GameRecord> gameRecords) {
        return new Statistic(
                gameRecords.size(),
                countGameResultsSuccess(gameRecords),
                countGameResultsFail(gameRecords),
                sumTotalDistance(gameRecords),
                sumTotalPlayTime(gameRecords),
                countUsedHits(gameRecords)
        );
    }

    private static int countGameResultsSuccess(final List<GameRecord> gameRecords) {
        return (int) gameRecords.stream()
                .filter(gameRecord -> gameRecord.getGameResult().getResultType() == SUCCESS)
                .count();
    }

    private static int countGameResultsFail(final List<GameRecord> gameRecords) {
        return (int) gameRecords.stream()
                .filter(gameRecord -> gameRecord.getGameResult().getResultType() == FAIL)
                .count();
    }

    private static int sumTotalDistance(final List<GameRecord> gameRecords) {
        return gameRecords.stream()
                .mapToInt(GameRecord::getDistance)
                .sum();
    }

    public static Duration sumTotalPlayTime(List<GameRecord> gameRecords) {
        Duration playTime = Duration.ZERO;
        for (GameRecord gameRecord : gameRecords) {
            playTime = playTime.plus(gameRecord.getTotalPlayTime());
        }
        return playTime;
    }

    private static int countUsedHits(final List<GameRecord> gameRecords) {
        return gameRecords.stream()
                .mapToInt(GameRecord::getHintUses)
                .sum();
    }

    public int durationToInteger(Duration duration){
        return (int) duration.toMinutes();
    }

    public int getGameCount() {
        return gameCount;
    }

    public int getSuccessGameCount() {
        return successGameCount;
    }

    public int getFailGameCount() {
        return failGameCount;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public Duration getTotalPlayTime() {
        return totalPlayTime;
    }

    public int getTotalUsedHintCount() {
        return totalUsedHintCount;
    }
}
