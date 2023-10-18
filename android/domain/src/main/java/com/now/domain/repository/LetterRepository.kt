package com.now.domain.repository

import com.now.domain.model.letter.Letter
import com.now.domain.model.letter.LetterPreview
import com.now.domain.model.type.LogType

interface LetterRepository {
    suspend fun postLetter(
        message: String,
        latitude: Double,
        longitude: Double,
    ): Letter

    suspend fun fetchNearbyLetters(latitude: Double, longitude: Double): List<LetterPreview>

    suspend fun fetchLetter(letterId: Long): Letter

    suspend fun fetchLetterLogs(gameId: Long, logType: LogType): List<Letter>
}
