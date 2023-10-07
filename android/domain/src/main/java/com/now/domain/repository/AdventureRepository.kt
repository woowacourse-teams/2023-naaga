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
    suspend fun fetchMyAdventures(): List<Adventure>
    suspend fun fetchAdventure(adventureId: Long): Adventure
    suspend fun fetchAdventureByStatus(status: AdventureStatus): List<Adventure>
    suspend fun beginAdventure(coordinate: Coordinate): Adventure

    suspend fun endGame(
        adventureId: Long,
        endType: AdventureEndType,
        coordinate: Coordinate,
    ): AdventureStatus

    suspend fun fetchAdventureResult(adventureId: Long): AdventureResult

    suspend fun fetchMyAdventureResults(
        sortBy: SortType,
        order: OrderType,
    ): List<AdventureResult>

    suspend fun fetchHint(
        adventureId: Long,
        hintId: Long,
    ): Hint

    suspend fun makeHint(
        adventureId: Long,
        coordinate: Coordinate,
    ): Hint
}
