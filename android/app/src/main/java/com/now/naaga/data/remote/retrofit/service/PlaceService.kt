package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.LikeCountDto
import com.now.naaga.data.remote.dto.PlaceDto
import com.now.naaga.data.remote.dto.PreferenceDto
import com.now.naaga.data.remote.dto.PreferenceStateDto
import com.now.naaga.data.remote.dto.post.PostPlaceDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    @POST("/temporary-places")
    suspend fun registerPlace(
        @PartMap postData: HashMap<String, RequestBody>,
        @Part imageFile: MultipartBody.Part,
    ): Response<PostPlaceDto>

    @GET("/places/{placeId}/likes/count")
    suspend fun getLikeCount(
        @Path("placeId") placedId: Int,
    ): Response<LikeCountDto>

    @DELETE("/places/{placeId}/likes/my")
    suspend fun deletePreference(
        @Path("placeId") placedId: Int,
    ): Response<Unit>

    @POST("/places/{placeId}/likes")
    suspend fun postPreference(
        @Path("placeId") placedId: Int,
        @Body body: PreferenceStateDto,
    ): Response<PreferenceDto>

    @GET("/places/{placeId}/likes/my")
    suspend fun getMyPreference(
        @Path("placeId") placedId: Int,
    ): Response<PreferenceStateDto>
}
