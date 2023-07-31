package com.now.naaga.data.repository

import com.now.domain.model.AdventureEndType
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Game
import com.now.domain.model.Hint
import com.now.domain.repository.AdventureRepository2
import com.now.domain.repository.OrderType
import com.now.domain.repository.SortType
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.dto.third.FinishGameDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse

class ThirdDemoAdventureRepository : AdventureRepository2 {
    override fun fetchMyAdventures(callback: (Result<List<Game>>) -> Unit) {
        val call = ServicePool.adventureService2.getMyGames()
        call.fetchNaagaResponse(
            onSuccess = { gameDtos ->
                val games: List<Game> = gameDtos.map { it.toDomain() }
                callback(Result.success(games))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchAdventure(adventureId: Long, callback: (Result<Game>) -> Unit) {
        val call = ServicePool.adventureService2.getGame(adventureId)
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(it.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchAdventureByStatus(status: AdventureStatus, callback: (Result<List<Game>>) -> Unit) {
        val call = ServicePool.adventureService2.getGamesByStatus(status.name)
        call.fetchNaagaResponse(
            onSuccess = { gameDtos ->
                val games = gameDtos.map { it.toDomain() }
                callback(Result.success(games))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun beginAdventure(coordinate: Coordinate, callback: (Result<Game>) -> Unit) {
        val call = ServicePool.adventureService2.beginGame(coordinate.toDto())
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(it.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun endGame(
        adventureId: Long,
        endType: AdventureEndType,
        coordinate: Coordinate,
        callback: (Result<AdventureStatus>) -> Unit,
    ) {
        val finishGameDto = FinishGameDto(endType.name, coordinate.toDto())
        val call = ServicePool.adventureService2.endGame(adventureId, finishGameDto)
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(AdventureStatus.getStatus(it.gameStatus))) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchAdventureResult(adventureId: Long, callback: (Result<AdventureResult>) -> Unit) {
        val call = ServicePool.adventureService2.getGameResult(adventureId)
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(it.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchMyAdventureResults(
        sortBy: SortType,
        order: OrderType,
        callback: (Result<List<AdventureResult>>) -> Unit,
    ) {
        val call = ServicePool.adventureService2.getMyGameResults(sortBy.name, order.name)
        call.fetchNaagaResponse(
            onSuccess = { adventureResultDtos ->
                val adventureResults = adventureResultDtos.map { it.toDomain() }
                callback(Result.success(adventureResults))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchHint(adventureId: Long, hintId: Long, callback: (Result<Hint>) -> Unit) {
        val call = ServicePool.adventureService2.getHint(adventureId, hintId)
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(it.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun makeHint(adventureId: Long, coordinate: Coordinate, callback: (Result<Hint>) -> Unit) {
        val call = ServicePool.adventureService2.requestHint(adventureId, coordinate.toDto())
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(it.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
