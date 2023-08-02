package com.now.naaga.data.repository

import com.now.domain.model.Rank
import com.now.domain.repository.RankRepository
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.retrofit.ServicePool.rankService
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse

class DefaultRankRepository : RankRepository {
    override fun getAllRanks(
        sortBy: String,
        order: String,
        callback: (Result<List<Rank>>) -> Unit,
    ) {
        val call = rankService.getAllRanks(sortBy, order)
        call.fetchNaagaResponse(
            onSuccess = { rankDtos ->
                callback(Result.success(rankDtos.map { it.toDomain() }))
            },
            onFailure = { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }

    override fun getMyRank(callback: (Result<Rank>) -> Unit) {
        val call = rankService.getMyRank()
        call.fetchNaagaResponse(
            onSuccess = { rankDto ->
                callback(Result.success(rankDto.toDomain()))
            },
            onFailure = { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }
}
