package com.now.naaga.data.remote.service

import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.CoordinateDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AdventureService {
    @POST("/games")
    fun beginGame(
        @Body coordinateDto: CoordinateDto,
    ): Call<Unit>

    @GET("/games")
    fun getGame(
        @Path("gameId") gameId: Long,
    ): Call<AdventureDto>

    @PATCH("/games")
    fun endGame(
        @Path("gameId") gameId: Long,
        @Body coordinateDto: CoordinateDto,
    ): Call<Unit>
}
