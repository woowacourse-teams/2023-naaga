package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.CoordinateDto
import com.now.naaga.data.remote.dto.EndedAdventureDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdventureService {
    @GET("/games")
    fun getGamesByStatus(
        @Query("status") status: String,
    ): Call<List<AdventureDto>>

    @POST("/games")
    fun beginGame(
        @Body coordinateDto: CoordinateDto,
    ): Call<Unit>

    @GET("/games/{gameId}")
    fun getGame(
        @Path("gameId") gameId: Long,
    ): Call<AdventureDto>

    @PATCH("/games/{gameId}")
    fun endGame(
        @Path("gameId") gameId: Long,
        @Body coordinateDto: CoordinateDto,
    ): Call<EndedAdventureDto>
}
