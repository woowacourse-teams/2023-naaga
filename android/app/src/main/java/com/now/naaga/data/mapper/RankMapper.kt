package com.now.naaga.data.mapper

import com.now.domain.model.Rank
import com.now.naaga.data.remote.dto.third.RankDto

fun RankDto.toDomain(): Rank {
    return Rank(
        player = player.toDomain(),
        rank = rank,
        percent = percentage,
    )
}
