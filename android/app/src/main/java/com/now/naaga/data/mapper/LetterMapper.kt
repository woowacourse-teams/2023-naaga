package com.now.naaga.data.mapper

import com.now.domain.model.letter.ClosedLetter
import com.now.naaga.data.remote.dto.ClosedLetterDto

fun ClosedLetterDto.toDomain(): ClosedLetter {
    return ClosedLetter(
        id = id,
        coordinate = coordinateDto.toDomain(),
    )
}
