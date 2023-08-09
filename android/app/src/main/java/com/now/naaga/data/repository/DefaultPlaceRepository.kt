package com.now.naaga.data.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.dto.PlaceDto
import com.now.naaga.data.remote.retrofit.ServicePool.placeService
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse
import com.now.naaga.data.remote.retrofit.fetchResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DefaultPlaceRepository : PlaceRepository {
    override fun fetchMyPlaces(
        sortBy: String,
        order: String,
        callback: (Result<List<Place>>) -> Unit,
    ) {
        val call = placeService.getMyPlace(sortBy, order)
        call.fetchResponse(
            onSuccess = { placeDtos: List<PlaceDto> ->
                callback(Result.success(placeDtos.map { it.toDomain() }))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun fetchPlace(placeId: Long, callback: (Result<Place>) -> Unit) {
        val call = placeService.getPlace(placeId)
        call.fetchResponse(
            onSuccess = { placeDto ->
                callback(Result.success(placeDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    // TODO : 업로드 기능 구현 시 fetchResponse로 변경해야 함
    override fun postPlace(
        name: String,
        description: String,
        coordinate: Coordinate,
        image: String,
        callback: (Result<Place>) -> Unit,
    ) {
        val file = File(image)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "imageFile",
            file.name,
            requestFile,
        )

        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val coordinatePart =
            Json.encodeToString(coordinate.toDto()).toRequestBody("application/json".toMediaTypeOrNull())

        val call = placeService.registerPlace(namePart, descriptionPart, coordinatePart, imagePart)

        call.fetchNaagaResponse(
            onSuccess = { placeDto -> callback(Result.success(placeDto.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
