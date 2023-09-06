package com.now.naaga.data.repository

import com.now.domain.model.Rank
import com.now.domain.repository.RankRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.dto.RankDto
import com.now.naaga.data.remote.retrofit.ServicePool.rankService
import com.now.naaga.data.remote.retrofit.fetchResponse

class DefaultRankRepository : RankRepository {
    override fun getAllRanks(
        sortBy: String,
        order: String,
        callback: (Result<List<Rank>>) -> Unit,
    ) {
        val call = rankService.getAllRanks(sortBy, order)
        call.fetchResponse(
            onSuccess = { rankDtos: List<RankDto> ->
                callback(Result.success(rankDtos.map { it.toDomain() }))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun getMyRank(callback: (Result<Rank>) -> Unit) {
        val call = rankService.getMyRank()
        call.fetchResponse(
            onSuccess = { rankDto ->
                callback(Result.success(rankDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }
}
