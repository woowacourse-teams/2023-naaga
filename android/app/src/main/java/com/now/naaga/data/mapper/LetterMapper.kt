package com.now.naaga.data.mapper

import com.now.domain.model.letter.Letter
import com.now.naaga.data.remote.dto.LetterDto

fun LetterDto.toDomain(): Letter {
    return Letter(
        id = id,
        player = player.toDomain(),
        coordinate = coordinateDto.toDomain(),
        message = message,
        registerDate = registerDate,
    )
}
