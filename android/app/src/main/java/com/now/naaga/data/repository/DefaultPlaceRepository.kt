package com.now.naaga.data.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.model.PreferenceState
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toPreferenceState
import com.now.naaga.data.remote.dto.PreferenceStateDto
import com.now.naaga.data.remote.retrofit.service.PlaceService
import com.now.naaga.util.extension.getValueOrThrow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DefaultPlaceRepository(
    private val placeService: PlaceService,
) : PlaceRepository {
    override suspend fun fetchMyPlaces(
        sortBy: String,
        order: String,
    ): List<Place> {
        val response = placeService.getMyPlace(sortBy, order)
        return response.getValueOrThrow().map { it.toDomain() }
    }

    override suspend fun fetchPlace(placeId: Long): Place {
        val response = placeService.getPlace(placeId)
        return response.getValueOrThrow().toDomain()
    }

    override suspend fun postPlace(
        name: String,
        description: String,
        coordinate: Coordinate,
        file: File,
    ): Place {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            KEY_IMAGE_FILE,
            file.name,
            requestFile,
        )

        val postData = HashMap<String, RequestBody>()
        postData[KEY_NAME] = name.toRequestBody("text/plain".toMediaTypeOrNull())
        postData[KEY_DESCRIPTION] = description.toRequestBody("text/plain".toMediaTypeOrNull())
        postData[KEY_LATITUDE] = coordinate.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        postData[KEY_LONGITUDE] = coordinate.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val response = placeService.registerPlace(postData, imagePart)
        return response.getValueOrThrow().toDomain()
    }

    override suspend fun deletePreference(placeId: Int) {
        placeService.deletePreference(placeId)
    }

    override suspend fun getLikeCount(placeId: Int): Int {
        val response = placeService.getLikeCount(placeId)
        return response.getValueOrThrow().placeLikeCount
    }

    override suspend fun getMyPreference(placeId: Int): PreferenceState {
        val response = placeService.getMyPreference(placeId)
        return response.getValueOrThrow().toPreferenceState()
    }

    override suspend fun postPreference(placeId: Int, preferenceState: PreferenceState): PreferenceState {
        val response = placeService.postPreference(placeId, PreferenceStateDto(preferenceState.name))
        return PreferenceState.valueOf(response.getValueOrThrow().type)
    }

    companion object {
        const val KEY_NAME = "name"
        const val KEY_DESCRIPTION = "description"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_IMAGE_FILE = "imageFile"
    }
}
