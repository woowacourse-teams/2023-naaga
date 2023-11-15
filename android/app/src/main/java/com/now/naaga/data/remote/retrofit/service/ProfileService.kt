package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.NicknameDto
import com.now.naaga.data.remote.dto.PlayerDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileService {
    @GET("/profiles/my")
    suspend fun getProfile(): Response<PlayerDto>

    @PATCH("/profiles/my")
    suspend fun modifyNickname(
        @Body nicknameDto: String,
    ): Response<NicknameDto>
}
