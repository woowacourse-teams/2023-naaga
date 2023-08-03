package com.now.naaga.data.mapper

import com.now.domain.model.Statistics
import com.now.naaga.data.remote.dto.StatisticsDto
import java.time.LocalTime

fun StatisticsDto.toDomain(): Statistics {
    return Statistics(
        adventureCount = gameCount,
        successCount = successGameCount,
        failureCount = failGameCount,
        totalDistance = totalDistance,
        totalPlayTime = LocalTime.parse(totalPlayTime),
        totalHintUses = totalUsedHintCount,
    )
}
