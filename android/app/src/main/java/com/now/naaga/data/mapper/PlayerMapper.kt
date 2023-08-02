package com.now.naaga.data.mapper

import com.now.domain.model.Player
import com.now.naaga.data.remote.dto.third.PlayerDto

fun PlayerDto.toDomain(): Player {
    return Player(
        id = id,
        nickname = nickname,
        score = totalScore,
    )
}
