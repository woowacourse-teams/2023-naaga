package com.now.domain.repository

import com.now.domain.model.Place
import java.io.File

interface PlaceRepository {
    fun fetchAllPlaces(
        sortBy: String,
        order: String,
        callback: (Result<List<Place>>) -> Unit,
    )

    fun fetchPlace(
        placeId: Long,
        callback: (Result<Place>) -> Unit,
    )

    fun registerPlace(
        place: Place,
        image: File,
        callback: (Result<Place>) -> Unit,
    )
}
