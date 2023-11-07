package com.now.domain.repository

import com.now.domain.model.PlatformAuth

interface AuthRepository {
    suspend fun logIn(platformAuth: PlatformAuth): Boolean
    suspend fun withdrawalMember()
    suspend fun logout()
    suspend fun refreshAccessToken()

    fun getAccessToken(): String?
    fun getRefreshToken(): String
    fun storeToken(accessToken: String, refreshToken: String)
}
