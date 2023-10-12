package com.now.domain.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.letter.ClosedLetter
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType

interface LetterRepository {
    suspend fun postLetter(
        message: String,
        latitude: String,
        longitude: Coordinate,
    ): OpenLetter

    suspend fun fetchNearbyLetters(latitude: String, longitude: Coordinate): List<ClosedLetter>

    suspend fun fetchLetter(letterId: Long): OpenLetter

    suspend fun fetchLetterLogs(gameId: Long, logType: LogType): List<OpenLetter>
}
