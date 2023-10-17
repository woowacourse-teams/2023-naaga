package com.now.domain.model.letter

import com.now.domain.model.Coordinate

data class ClosedLetter(
    val id: Long,
    val coordinate: Coordinate,
) {
    var isNearBy = false

    fun isNearBy(other: Coordinate) {
        isNearBy = coordinate.isNearBy(other)
    }
}
