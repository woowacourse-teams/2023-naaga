package com.now.naaga.data.repository

import com.now.domain.model.Place
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ServicePool.placeService
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DefaultPlaceRepository : PlaceRepository {
    override fun fetchAllPlaces(
        sortBy: String,
        order: String,
        callback: (Result<List<Place>>) -> Unit,
    ) {
        val call = placeService.getMyPlace(sortBy, order)

        call.fetchNaagaResponse(
            onSuccess = { places -> callback(Result.success(places.map { it.toDomain() })) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchPlace(placeId: Long, callback: (Result<Place>) -> Unit) {
        val call = placeService.getPlace(placeId)

        call.fetchNaagaResponse(
            onSuccess = { place -> callback(Result.success(place.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun postPlace(place: Place, image: File, callback: (Result<Place>) -> Unit) {
        val imagePart = MultipartBody.Part.createFormData(
            "imageFile",
            image.name,
            image.asRequestBody("image/jpeg".toMediaTypeOrNull()),
        )
        val call = placeService.registerPlace(place.toDto(), imagePart)

        call.fetchNaagaResponse(
            onSuccess = { placeDto -> callback(Result.success(placeDto.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
