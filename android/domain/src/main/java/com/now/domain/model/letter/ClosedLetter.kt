package com.now.domain.model.letter

import com.now.domain.model.Coordinate
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class ClosedLetter(
    val id: Long,
    val coordinate: Coordinate,
) {
    var isNearBy = false

    fun isNearBy(myCoordinate: Coordinate) {
        val rad = Math.PI / 180
        val radLat1 = rad * myCoordinate.latitude
        val radLat2 = rad * coordinate.latitude
        val radDist = rad * (myCoordinate.longitude - coordinate.longitude)

        var distance = sin(radLat1) * sin(radLat2)
        distance += cos(radLat1) * cos(radLat2) * cos(radDist)
        val ret = 6371000.0 * acos(distance)

        isNearBy = ret.roundToInt() <= 50
    }
}
