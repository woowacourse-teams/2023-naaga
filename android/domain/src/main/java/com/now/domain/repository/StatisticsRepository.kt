package com.now.domain.repository

import com.now.domain.model.Statistics

interface StatisticsRepository {
    fun getMyStatistics(callback: (Result<Statistics>) -> Unit)
}
