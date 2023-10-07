package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.StatisticsDto
import retrofit2.Response
import retrofit2.http.GET

interface StatisticsService {
    @GET("/statistics/my")
    suspend fun getMyStatistics(): Response<StatisticsDto>
}
