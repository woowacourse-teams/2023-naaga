package com.now.domain.repository

import com.now.domain.model.Rank

interface RankRepository {
    suspend fun getAllRanks(
        sortBy: String,
        order: String,
    ): List<Rank>

    suspend fun getMyRank(): Rank
}
