package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.NaagaAuthDto
import com.now.naaga.data.remote.dto.PlatformAuthDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/auth")
    suspend fun requestAuth(
        @Body platformAuthDto: PlatformAuthDto,
    ): Response<NaagaAuthDto>

    @DELETE("/auth/unlink")
    suspend fun withdrawalMember(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>

    @DELETE("/auth")
    suspend fun requestLogout(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>
}
