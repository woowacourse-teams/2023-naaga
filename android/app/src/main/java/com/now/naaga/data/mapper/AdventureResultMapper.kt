package com.now.naaga.data.mapper

import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureResultType
import com.now.naaga.data.remote.dto.third.AdventureResultDto
import java.time.LocalDateTime
import java.time.LocalTime

fun AdventureResultDto.toDomain(): AdventureResult {
    return AdventureResult(
        id = id,
        gameId = gameId,
        destination = destination.toDomain(),
        resultType = AdventureResultType.findByName(resultType),
        score = score,
        playTime = LocalTime.parse(totalPlayTime),
        distance = distance,
        hintUses = hintUses,
        tryCount = tryCount,
        beginTime = LocalDateTime.parse(startTime),
        endTime = LocalDateTime.parse(finishTime),
    )
}
