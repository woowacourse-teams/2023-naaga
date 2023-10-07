package com.now.naaga.data.repository

import com.now.domain.model.Statistics
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.retrofit.service.StatisticsService
import com.now.naaga.util.getValueOrThrow

class DefaultStatisticsRepository(
    private val statisticsService: StatisticsService,
) : StatisticsRepository {
    override suspend fun getMyStatistics(): Statistics {
        val response = statisticsService.getMyStatistics()
        return response.getValueOrThrow().toDomain()
    }
}
