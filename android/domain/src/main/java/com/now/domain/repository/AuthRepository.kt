package com.now.domain.repository

import com.now.domain.model.PlatformAuth

interface AuthRepository {
    suspend fun getToken(
        platformAuth: PlatformAuth,
    ): Boolean

    suspend fun withdrawalMember(): Boolean
}
