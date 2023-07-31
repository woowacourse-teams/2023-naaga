package com.now.domain.repository

import com.now.domain.model.AdventureEndType
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Game
import com.now.domain.model.Hint

interface AdventureRepository2 {
    fun fetchMyAdventures(callback: (Result<List<Game>>) -> Unit)

    fun fetchAdventure(adventureId: Long, callback: (Result<Game>) -> Unit)

    fun fetchAdventureByStatus(status: AdventureStatus, callback: (Result<List<Game>>) -> Unit)

    fun beginAdventure(coordinate: Coordinate, callback: (Result<Game>) -> Unit)

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
