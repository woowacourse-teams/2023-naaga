package com.now.naaga.data.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.dto.PlaceDto
import com.now.naaga.data.remote.retrofit.ServicePool.placeService
import com.now.naaga.data.remote.retrofit.fetchResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            KEY_IMAGE_FILE,
            file.name,
            requestFile,
        )

        val postData = HashMap<String, RequestBody>()
        postData[KEY_NAME] = name.toRequestBody("text/plain".toMediaTypeOrNull())
        postData[KEY_DESCRIPTION] = description.toRequestBody("text/plain".toMediaTypeOrNull())
        postData[KEY_LATITUDE] = coordinate.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        postData[KEY_LONGITUDE] = coordinate.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val call = placeService.registerPlace(postData, imagePart)

        call.fetchResponse(
            onSuccess = { placeDto ->
                callback(Result.success(placeDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    companion object {
        const val KEY_NAME = "name"
        const val KEY_DESCRIPTION = "description"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_IMAGE_FILE = "imageFile"
    }
}
