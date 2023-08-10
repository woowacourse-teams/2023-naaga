package com.now.domain.repository

import com.now.domain.model.NaagaAuth
import com.now.domain.model.PlatformAuth

interface AuthRepository {
    fun getToken(
        platformAuth: PlatformAuth,
        callback: (Result<NaagaAuth>) -> Unit,
    )
}
