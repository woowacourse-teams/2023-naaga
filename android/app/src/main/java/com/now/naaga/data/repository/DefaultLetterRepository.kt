package com.now.naaga.data.repository

import com.now.domain.model.letter.Letter
import com.now.domain.model.letter.LetterPreview
import com.now.domain.model.type.LogType
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.dto.post.PostLetterDto
import com.now.naaga.data.remote.retrofit.service.LetterService
import com.now.naaga.util.extension.getValueOrThrow

class DefaultLetterRepository(
    private val letterService: LetterService,
) : LetterRepository {
    override suspend fun postLetter(message: String, latitude: Double, longitude: Double): Letter {
        val response = letterService.registerLetter(PostLetterDto(message, latitude, longitude)).getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun fetchNearbyLetters(latitude: Double, longitude: Double): List<LetterPreview> {
        val response = letterService.getNearbyLetters(latitude, longitude).getValueOrThrow()
        return response.map { it.toDomain() }
    }

    override suspend fun fetchLetter(letterId: Long): Letter {
        val response = letterService.getLetter(letterId).getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun fetchLetterLogs(gameId: Long, logType: LogType): List<Letter> {
        return letterService.getInGameLetters(gameId, logType.name).getValueOrThrow().map { it.toDomain() }
    }
}
