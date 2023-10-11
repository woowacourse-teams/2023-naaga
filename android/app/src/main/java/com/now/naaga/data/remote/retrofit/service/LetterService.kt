package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.ClosedLetterDto
import com.now.naaga.data.remote.dto.LetterLogDto
import com.now.naaga.data.remote.dto.OpenLetterDto
import com.now.naaga.data.remote.dto.post.PostLetterDto
import com.now.naaga.data.remote.dto.post.PostLetterLogDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LetterService {
    @POST("/letters")
    suspend fun registerLetter(
        @Body postLetterDto: PostLetterDto,
    ): Response<OpenLetterDto>

    @GET("/letters/nearby")
    suspend fun getNearbyLetters(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
    ): Response<List<ClosedLetterDto>>

    @GET("/letters/{letterId}")
    suspend fun getLetter(
        @Path("letterId") letterId: Long,
    ): Response<OpenLetterDto>

    @POST("/letterlogs")
    suspend fun registerInGameLetter(
        @Body postLetterLogDto: PostLetterLogDto,
    ): Response<LetterLogDto>

    @GET("/letterlogs")
    suspend fun getInGameLetters(
        @Query("gameId") gameId: Long,
        @Query("logType") logType: String,
    ): Response<List<OpenLetterDto>>
}
