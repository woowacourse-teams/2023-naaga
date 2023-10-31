package com.now.domain.model

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Place(
    val id: Long,
    val name: String,
    val coordinate: Coordinate,
    val image: String,
    val description: String,
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

    fun isNearBy(coordinate: Coordinate): Boolean {
        return getDistance(coordinate) <= NEARBY_STANDARD
    }

    companion object {
        const val NEARBY_STANDARD = 70L
    }
}
