package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.third.StatisticDto
import retrofit2.Call
import retrofit2.http.GET

interface StatisticsService {
    @GET("/statistics/my")
    fun getMyStatistics(): Call<StatisticDto>
}
