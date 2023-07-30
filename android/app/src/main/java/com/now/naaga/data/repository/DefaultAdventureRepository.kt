package com.now.naaga.data.repository

import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ERROR_500
import com.now.naaga.data.remote.retrofit.ERROR_NOT_400_500
import com.now.naaga.data.remote.retrofit.ServicePool.adventureService
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse
import com.now.naaga.data.remote.retrofit.getFailureDto
import com.now.naaga.data.remote.retrofit.isFailure400
import com.now.naaga.data.remote.retrofit.isFailure500
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DefaultAdventureRepository : AdventureRepository {
    override fun fetchAdventuresByStatus(
        status: AdventureStatus,
        callback: (Result<List<Adventure>>) -> Unit,
    ) {
        val call = adventureService.getGamesByStatus(status.name)
        call.fetchNaagaResponse(
            onSuccess = { adventures ->
                if (adventures == null) {
                    callback(Result.failure(NaagaThrowable.NaagaUnknownError("null 값이 넘어왔습니다")))
                    return@fetchNaagaResponse
                }
                callback(Result.success(adventures.map { it.toDomain() }))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun beginAdventure(coordinate: Coordinate, callback: (Result<Long>) -> Unit) {
        val call = adventureService.beginGame(coordinate.toDto())

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val headerEmptyError = NaagaThrowable.NaagaUnknownError("Location 헤더가 비어있습니다.")
                    val responsePath: String = response.headers()["Location"]
                        ?: return callback(Result.failure(headerEmptyError))
                    val adventureId: Long = responsePath.substringAfterLast("/").toLongOrNull()
                        ?: return callback(Result.failure(headerEmptyError))
                    callback(Result.success(adventureId))
                } else {
                    if (response.isFailure400()) {
                        callback(Result.failure(response.getFailureDto().getThrowable()))
                        return
                    }
                    if (response.isFailure500()) {
                        callback(Result.failure(NaagaThrowable.NaagaUnknownError(ERROR_500)))
                        return
                    }
                    callback(Result.failure(NaagaThrowable.NaagaUnknownError(ERROR_NOT_400_500)))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(Result.failure(NaagaThrowable.ServerConnectFailure()))
            }
        })
    }

    override fun getAdventure(adventureId: Long, callback: (Result<Adventure>) -> Unit) {
        val call = adventureService.getGame(adventureId)

        call.fetchNaagaResponse(
            { adventureDto ->
                if (adventureDto != null) {
                    callback(Result.success(adventureDto.toDomain()))
                }
            },
            { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }

    override fun endAdventure(
        adventureId: Long,
        coordinate: Coordinate,
        callback: (Result<AdventureStatus>) -> Unit,
    ) {
        val call = adventureService.endGame(adventureId, coordinate.toDto())
        call.fetchNaagaResponse(
            { EndedAdventureDto ->
                if (EndedAdventureDto != null) {
                    callback(Result.success(EndedAdventureDto.toDomain()))
                }
            },
            { callback(Result.failure(NaagaThrowable.ServerConnectFailure())) },
        )
    }
}
