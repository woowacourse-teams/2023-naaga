package com.now.naaga.data.repository

import com.now.domain.model.Player
import com.now.domain.repository.ProfileRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.dto.NicknameDto
import com.now.naaga.data.remote.retrofit.service.ProfileService
import com.now.naaga.util.extension.getValueOrThrow

class DefaultProfileRepository(
    private val profileService: ProfileService,
) : ProfileRepository {
    override suspend fun fetchProfile(): Player {
        val response = profileService.getProfile().getValueOrThrow()
        return response.toDomain()
    }

    override suspend fun modifyNickname(nickname: String): String {
        val nicknameDto = NicknameDto(nickname)
        val response = profileService.modifyNickname(nicknameDto).getValueOrThrow()
        return response.nickname
    }
}
