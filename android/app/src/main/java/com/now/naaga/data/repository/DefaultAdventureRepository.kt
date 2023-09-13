package com.now.naaga.data.repository

import com.now.domain.model.Adventure
import com.now.domain.model.AdventureEndType
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.domain.model.OrderType
import com.now.domain.model.SortType
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.dto.FinishGameDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.util.getValueOrThrow

class DefaultAdventureRepository : AdventureRepository {
    override suspend fun fetchMyAdventures(): List<Adventure> {
        val response = ServicePool.adventureService.getMyGames().getValueOrThrow()
        return response.map { it.toDomain() }
    }

    override suspend fun fetchAdventure(adventureId: Long): Adventure {
        val response = ServicePool.adventureService.getGame(adventureId).getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun fetchAdventureByStatus(status: AdventureStatus): List<Adventure> {
        val response = ServicePool.adventureService.getGamesByStatus(status.name).getValueOrThrow()
        return response.map { it.toDomain() }
    }

    override suspend fun beginAdventure(coordinate: Coordinate): Adventure {
        val response = ServicePool.adventureService.beginGame(coordinate.toDto()).getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun endGame(
        adventureId: Long,
        endType: AdventureEndType,
        coordinate: Coordinate,
    ): AdventureStatus {
        val finishGameDto = FinishGameDto(endType.name, coordinate.toDto())
        val response = ServicePool.adventureService.endGame(adventureId, finishGameDto).getValueOrThrow()
        return AdventureStatus.getStatus(response.gameStatus)
    }

    override suspend fun fetchAdventureResult(adventureId: Long): AdventureResult {
        val response = ServicePool.adventureService.getGameResult(adventureId).getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun fetchMyAdventureResults(
        sortBy: SortType,
        order: OrderType,
    ): List<AdventureResult> {
        val response = ServicePool.adventureService.getMyGameResults(sortBy.name, order.name).getValueOrThrow()
        return response.map { it.toDomain() }
    }

    override suspend fun fetchHint(adventureId: Long, hintId: Long): Hint {
        val response = ServicePool.adventureService.getHint(adventureId, hintId).getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun makeHint(adventureId: Long, coordinate: Coordinate): Hint {
        val response = ServicePool.adventureService.requestHint(adventureId, coordinate.toDto()).getValueOrThrow()
        return response.toDomain()
    }
}
