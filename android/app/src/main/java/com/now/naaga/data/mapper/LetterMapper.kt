package com.now.naaga.data.mapper

import com.now.domain.model.letter.ClosedLetter
import com.now.domain.model.letter.OpenLetter
import com.now.naaga.data.remote.dto.ClosedLetterDto
import com.now.naaga.data.remote.dto.OpenLetterDto

fun ClosedLetterDto.toDomain(): ClosedLetter {
    return ClosedLetter(
        id = id,
        coordinate = coordinateDto.toDomain(),
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
