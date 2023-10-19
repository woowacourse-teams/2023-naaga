package com.now.domain.model

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Coordinate(
    val latitude: Double,
    val longitude: Double,
) {

    fun isNearBy(other: Coordinate): Boolean {
        val rad = Math.PI / 180
        val radLat1 = rad * other.latitude
        val radLat2 = rad * latitude
        val radDist = rad * (other.longitude - longitude)

        var distance = sin(radLat1) * sin(radLat2)
        distance += cos(radLat1) * cos(radLat2) * cos(radDist)
        val ret = 6371000.0 * acos(distance)

        return ret.roundToInt() <= 50
    }
}
