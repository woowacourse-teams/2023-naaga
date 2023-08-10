package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.NaagaAuthDto
import com.now.naaga.data.remote.dto.PlatformAuthDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth")
    fun requestAuth(
        @Body platformAuthDto: PlatformAuthDto,
    ): Call<NaagaAuthDto>
}
