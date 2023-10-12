package com.now.domain.model.letter

import com.now.domain.model.Coordinate
import com.now.domain.model.Player

data class OpenLetter(
    val id: Long,
    val player: Player,
    val coordinate: Coordinate,
    val message: String,
    val registerDate: String,
)
