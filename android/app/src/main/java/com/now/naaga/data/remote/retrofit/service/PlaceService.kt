package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.PlaceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceService {
    @GET("/places")
    suspend fun getMyPlace(
        @Query("sort-by") sort: String,
        @Query("order") order: String,
    ): Response<List<PlaceDto>>

    @GET("/places/{placeId}")
    suspend fun getPlace(
        @Path("placeId") placeId: Long,
    ): Response<PlaceDto>

    @Multipart
    @POST("/places")
    suspend fun registerPlace(
        @PartMap postData: HashMap<String, RequestBody>,
        @Part imageFile: MultipartBody.Part,
    ): Response<PlaceDto>
}
