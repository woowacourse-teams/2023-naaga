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
            { rankDtos ->
                if (rankDtos != null) {
                    callback(Result.success(rankDtos.map { it.toDomain() }))
                }
            },
            { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }

    override fun getMyRank(callback: (Result<Rank>) -> Unit) {
        val call = rankService.getMyRank()
        call.fetchNaagaResponse(
            { rankDto ->
                if (rankDto != null) {
                    callback(Result.success(rankDto.toDomain()))
                }
            },
            { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }
}
