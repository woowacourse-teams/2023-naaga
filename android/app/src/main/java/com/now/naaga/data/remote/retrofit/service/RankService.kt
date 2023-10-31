package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.RankDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RankService {
    @GET("/ranks")
    suspend fun getAllRanks(
        @Query("sort-by") sortBy: String,
        @Query("order") order: String,
    ): Response<List<RankDto>>

    @GET("/ranks/my")
    suspend fun getMyRank(): Response<RankDto>
}
