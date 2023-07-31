package com.now.naaga.data.repository

import com.now.domain.model.Statistics
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.retrofit.ServicePool.statisticsService
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse

class DefaultStatisticsRepository : StatisticsRepository {
    override fun getMyStatistics(callback: (Result<Statistics>) -> Unit) {
        val call = statisticsService.getMyStatistics()
        call.fetchNaagaResponse(
            { statisticsDto ->
                if (statisticsDto != null) {
                    callback(Result.success(statisticsDto.toDomain()))
                }
            },
            { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }
}
