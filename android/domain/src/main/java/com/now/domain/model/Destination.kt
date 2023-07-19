package com.now.domain.model

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Destination(
    val id: Int,
    val coordinate: Coordinate,
    val image: String,
) {
    fun getDistance(other: Coordinate): Int {
        val rad = Math.PI / 180
        val radLat1 = rad * coordinate.latitude
        val radLat2 = rad * other.latitude
        val radDist = rad * (coordinate.longitude - other.longitude)

        var distance = sin(radLat1) * sin(radLat2)
        distance += cos(radLat1) * cos(radLat2) * cos(radDist)
        val ret = 6371000.0 * acos(distance)

        return ret.roundToInt()
    }

    fun isArrived(coordinate: Coordinate): Boolean {
        return ARRIVAL_STANDARD > getDistance(coordinate)
    }

    companion object {
        const val ARRIVAL_STANDARD = 50L
    }
}
