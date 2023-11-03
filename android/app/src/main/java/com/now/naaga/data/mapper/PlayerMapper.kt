package com.now.naaga.data.mapper

import com.now.domain.model.Player
import com.now.naaga.data.remote.dto.PlayerDto

fun PlayerDto.toDomain(): Player {
    return Player(
        id = id,
        nickname = nickname,
        score = totalScore,
    )
}

fun Player.toDto(): PlayerDto {
    return PlayerDto(
        id = id,
        nickname = nickname,
        totalScore = score,
    )
}
