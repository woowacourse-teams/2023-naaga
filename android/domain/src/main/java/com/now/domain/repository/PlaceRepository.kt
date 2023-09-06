package com.now.domain.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Place

interface PlaceRepository {
    fun fetchMyPlaces(
        sortBy: String,
        order: String,
        callback: (Result<List<Place>>) -> Unit,
    )

    fun fetchPlace(
        placeId: Long,
        callback: (Result<Place>) -> Unit,
    )

    fun postPlace(
        name: String,
        description: String,
        coordinate: Coordinate,
        image: String,
        callback: (Result<Place>) -> Unit,
    )
}
