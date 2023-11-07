package com.now.naaga.data.mapper

import com.now.domain.model.letter.LetterPreview
import com.now.naaga.data.remote.dto.LetterPreviewDto

fun LetterPreviewDto.toDomain(): LetterPreview {
    return LetterPreview(
        id = id,
        coordinate = coordinateDto.toDomain(),
    )
}
