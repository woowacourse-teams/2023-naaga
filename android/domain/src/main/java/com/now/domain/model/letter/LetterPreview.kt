package com.now.domain.model.letter

import com.now.domain.model.Coordinate

data class LetterPreview(
    val id: Long,
    val coordinate: Coordinate,
    val isNearBy: Boolean = false,
)
