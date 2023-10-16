package com.now.domain.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import java.io.File

interface PlaceRepository {
    suspend fun fetchMyPlaces(
        sortBy: String,
        order: String,
    ): List<Place>

    suspend fun fetchPlace(
        placeId: Long,
    ): Place

    suspend fun postPlace(
        name: String,
        description: String,
        coordinate: Coordinate,
        file: File,
    ): Place
}
