package com.now.domain.repository

import com.now.domain.model.Statistics

interface StatisticsRepository {
    suspend fun getMyStatistics(): Statistics
}
