package com.now.naaga.data.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.letter.ClosedLetter
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.remote.retrofit.service.LetterService

class DefaultLetterRepository(
    private val letterService: LetterService,
) : LetterRepository {
    override suspend fun postLetter(message: String, latitude: String, longitude: Coordinate): OpenLetter {
        TODO("Not yet implemented")
    }

    override suspend fun fetchNearbyLetters(latitude: String, longitude: Coordinate): List<ClosedLetter> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchLetter(letterId: Long): OpenLetter {
        TODO("Not yet implemented")
    }

    override suspend fun postInGameLetter(gameId: Long, letterId: Long, logType: LogType) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchReadLetter(gameId: Long, logType: LogType): List<OpenLetter> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchWriteLetter(gameId: Long, logType: LogType): List<OpenLetter> {
        TODO("Not yet implemented")
    }
}
