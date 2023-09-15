package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.NaagaAuthDto
import com.now.naaga.data.remote.dto.PlatformAuthDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthService {
    @POST("/auth")
    suspend fun requestAuth(
        @Body platformAuthDto: PlatformAuthDto,
    ): Response<NaagaAuthDto>

    @DELETE("/auth")
    suspend fun requestLogout(): Response<Unit>
}
