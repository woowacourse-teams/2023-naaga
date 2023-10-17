package com.now.naaga.data.mapper

import com.now.domain.model.letter.OpenLetter
import com.now.naaga.data.remote.dto.OpenLetterDto

fun OpenLetterDto.toDomain(): OpenLetter {
    return OpenLetter(
        id = id,
        player = player.toDomain(),
        coordinate = coordinateDto.toDomain(),
        message = message,
        registerDate = registerDate,
    )
}
