package com.now.domain.repository

import com.now.domain.model.PlatformAuth

interface AuthRepository {
    fun getToken(
        platformAuth: PlatformAuth,
        callback: (Result<Boolean>) -> Unit,
    )

    fun refreshToken(callback: (Result<Boolean>) -> Unit)
}
