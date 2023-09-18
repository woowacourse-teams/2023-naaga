package com.now.naaga.data.repository

import com.now.domain.model.Rank
import com.now.domain.repository.RankRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.retrofit.ServicePool.rankService
import com.now.naaga.util.getValueOrThrow

class DefaultRankRepository : RankRepository {
    override suspend fun getAllRanks(
        sortBy: String,
        order: String,
    ): List<Rank> {
        val response = rankService.getAllRanks(sortBy, order)
        return response.getValueOrThrow().map { it.toDomain() }
    }

    override suspend fun getMyRank(): Rank {
        val response = rankService.getMyRank()
        return response.getValueOrThrow().toDomain()
    }
}
