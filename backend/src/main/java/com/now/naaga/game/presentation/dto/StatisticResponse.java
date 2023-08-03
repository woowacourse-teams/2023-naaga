package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Statistic;

public record StatisticResponse(int gameCount,
                                int successGameCount,
                                int failGameCount,
                                int totalDistance,
                                String totalPlayTime,
                                int totalUsedHintCount) {
    public static StatisticResponse from(final Statistic statistic) {
        return new StatisticResponse(
                statistic.getGameCount(),
                statistic.getSuccessGameCount(),
                statistic.getFailGameCount(),
                statistic.getTotalDistance(),
                statistic.durationToString(statistic.getTotalPlayTime()),
                statistic.getTotalUsedHintCount()
        );
    }
}
