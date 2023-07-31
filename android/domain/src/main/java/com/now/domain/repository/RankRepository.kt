package com.now.domain.repository

import com.now.domain.model.Rank

interface RankRepository {
    fun getAllRanks(
        sortBy: String,
        order: String,
        callback: (Result<List<Rank>>) -> Unit,
    )

    fun getMyRank(callback: (Result<Rank>) -> Unit)
}
