package com.now.domain.model.letter

import com.now.domain.model.Coordinate

data class OpenLetter(
    val id: Long,
    val writer: Writer,
    val coordinate: Coordinate,
    val message: Message,
    val registerDate: String,
)
