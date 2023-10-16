package com.now.naaga.data.repository

import com.now.domain.model.letter.ClosedLetter
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.retrofit.service.LetterService
import com.now.naaga.util.getValueOrThrow

class DefaultLetterRepository(
    private val letterService: LetterService,
) : LetterRepository {
    override suspend fun postLetter(message: String, latitude: Double, longitude: Double): OpenLetter {
        TODO("Not yet implemented")
    }

    override suspend fun fetchNearbyLetters(latitude: Double, longitude: Double): List<ClosedLetter> {
        val response = letterService.getNearbyLetters(latitude, longitude).getValueOrThrow()
        return response.map { it.toDomain() }
    }

    override suspend fun fetchLetter(letterId: Long): OpenLetter {
        TODO("Not yet implemented")
    }

    override suspend fun fetchLetterLogs(gameId: Long, logType: LogType): List<OpenLetter> {
        TODO("Not yet implemented")
    }
}