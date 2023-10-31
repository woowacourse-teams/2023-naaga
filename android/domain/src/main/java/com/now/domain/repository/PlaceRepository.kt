package com.now.domain.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.model.PreferenceState
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

    suspend fun postPreference(placeId: Int, preferenceState: PreferenceState): PreferenceState
    suspend fun getMyPreference(placeId: Int): PreferenceState
    suspend fun deletePreference(placeId: Int)
    suspend fun getLikeCount(placeId: Int): Int
}
