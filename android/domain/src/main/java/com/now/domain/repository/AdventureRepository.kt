package com.now.domain.repository

import com.now.domain.model.Adventure
import com.now.domain.model.AdventureEndType
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.domain.model.OrderType
import com.now.domain.model.SortType

interface AdventureRepository {
    fun fetchMyAdventures(callback: (Result<List<Adventure>>) -> Unit)

    fun fetchAdventure(adventureId: Long, callback: (Result<Adventure>) -> Unit)

    fun fetchAdventureByStatus(status: AdventureStatus, callback: (Result<List<Adventure>>) -> Unit)

    fun beginAdventure(coordinate: Coordinate, callback: (Result<Adventure>) -> Unit)

    fun endGame(
        adventureId: Long,
        endType: AdventureEndType,
        coordinate: Coordinate,
        callback: (Result<AdventureStatus>) -> Unit,
    )

    fun fetchAdventureResult(adventureId: Long, callback: (Result<AdventureResult>) -> Unit)

    fun fetchMyAdventureResults(
        sortBy: SortType,
        order: OrderType,
        callback: (Result<List<AdventureResult>>) -> Unit,
    )

    fun fetchHint(
        adventureId: Long,
        hintId: Long,
        callback: (Result<Hint>) -> Unit,
    )

    fun makeHint(
        adventureId: Long,
        coordinate: Coordinate,
        callback: (Result<Hint>) -> Unit,
    )
}
