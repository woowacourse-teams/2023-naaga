package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Statistic;

import java.time.LocalDateTime;

public record StatisticResponse(int gameCount,
                                int successGameCount,
                                int failGameCount,
                                int totalDistance,
                                LocalDateTime totalPlayTime,
                                int totalUsedHintCount) {
    public static StatisticResponse from(final Statistic statistic) {
        return new StatisticResponse(
                statistic.getGameCount(),
                statistic.getSuccessGameCount(),
                statistic.getFailGameCount(),
                statistic.getTotalDistance(),
                statistic.getTotalPlayTime(),
                statistic.getTotalUsedHintCount()
        );
    }
}
