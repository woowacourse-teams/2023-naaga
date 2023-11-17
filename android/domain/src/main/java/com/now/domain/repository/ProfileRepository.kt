package com.now.domain.repository

import com.now.domain.model.Player

interface ProfileRepository {
    suspend fun fetchProfile(): Player

    suspend fun modifyNickname(nickname: String): String
}
