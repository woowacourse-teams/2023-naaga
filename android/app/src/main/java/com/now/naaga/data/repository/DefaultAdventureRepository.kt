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
import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.FinishGameDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.data.remote.retrofit.fetchResponse

class DefaultAdventureRepository : AdventureRepository {
    override fun fetchMyAdventures(callback: (Result<List<Adventure>>) -> Unit) {
        val call = ServicePool.adventureService.getMyGames()
        call.fetchResponse(
            onSuccess = { adventureDtos: List<AdventureDto> ->
                callback(Result.success(adventureDtos.map { it.toDomain() }))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun fetchAdventure(adventureId: Long, callback: (Result<Adventure>) -> Unit) {
        val call = ServicePool.adventureService.getGame(adventureId)
        call.fetchResponse(
            onSuccess = { adventureDto ->
                callback(Result.success(adventureDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun fetchAdventureByStatus(status: AdventureStatus, callback: (Result<List<Adventure>>) -> Unit) {
        val call = ServicePool.adventureService.getGamesByStatus(status.name)
        call.fetchResponse(
            onSuccess = { adventureDtos: List<AdventureDto> ->
                callback(Result.success(adventureDtos.map { it.toDomain() }))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun beginAdventure(coordinate: Coordinate, callback: (Result<Adventure>) -> Unit) {
        val call = ServicePool.adventureService.beginGame(coordinate.toDto())
        call.fetchResponse(
            onSuccess = { adventureDto ->
                callback(Result.success(adventureDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun endGame(
        adventureId: Long,
        endType: AdventureEndType,
        coordinate: Coordinate,
        callback: (Result<AdventureStatus>) -> Unit,
    ) {
        val finishGameDto = FinishGameDto(endType.name, coordinate.toDto())
        val call = ServicePool.adventureService.endGame(adventureId, finishGameDto)
        call.fetchResponse(
            onSuccess = { adventureStatusDto ->
                callback(Result.success(AdventureStatus.getStatus(adventureStatusDto.gameStatus)))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun fetchAdventureResult(adventureId: Long, callback: (Result<AdventureResult>) -> Unit) {
        val call = ServicePool.adventureService.getGameResult(adventureId)
        call.fetchResponse(
            onSuccess = { adventureResultDto ->
                callback(Result.success(adventureResultDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun fetchMyAdventureResults(
        sortBy: SortType,
        order: OrderType,
        callback: (Result<List<AdventureResult>>) -> Unit,
    ) {
        val call = ServicePool.adventureService.getMyGameResults(sortBy.name, order.name)
        call.fetchResponse(
            onSuccess = { adventureResultDtos ->
                callback(Result.success(adventureResultDtos.map { it.toDomain() }))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun fetchHint(adventureId: Long, hintId: Long, callback: (Result<Hint>) -> Unit) {
        val call = ServicePool.adventureService.getHint(adventureId, hintId)
        call.fetchResponse(
            onSuccess = { hintDto ->
                callback(Result.success(hintDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }

    override fun makeHint(adventureId: Long, coordinate: Coordinate, callback: (Result<Hint>) -> Unit) {
        val call = ServicePool.adventureService.requestHint(adventureId, coordinate.toDto())
        call.fetchResponse(
            onSuccess = { hintDto ->
                callback(Result.success(hintDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }
}
