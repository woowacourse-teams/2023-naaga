package com.now.naaga.data.mapper

import com.now.domain.model.letter.OpenLetter
import com.now.naaga.data.remote.dto.OpenLetterDto

fun OpenLetter.toDto(): OpenLetterDto {
    return OpenLetterDto(
        id = id,
        player = player.toDto(),
        coordinateDto = coordinate.toDto(),
        message = message,
        registerDate = registerDate,
    )
}

fun OpenLetterDto.toDomain(): OpenLetter {
    return OpenLetter(
        id = id,
        player = player.toDomain(),
        coordinate = coordinateDto.toDomain(),
        message = message,
        registerDate = registerDate,
    )
}
