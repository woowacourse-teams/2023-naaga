package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.AdventureResultDto
import com.now.naaga.data.remote.dto.AdventureStatusDto
import com.now.naaga.data.remote.dto.CoordinateDto
import com.now.naaga.data.remote.dto.FinishGameDto
import com.now.naaga.data.remote.dto.HintDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdventureService {
    @GET("/games")
    fun getMyGames(): Call<List<AdventureDto>>

    @GET("/games/{gameId}")
    fun getGame(
        @Path("gameId") gameId: Long,
    ): Call<AdventureDto>

    @GET("/games")
    fun getGamesByStatus(
        @Query("status") status: String,
    ): Call<List<AdventureDto>>

    @POST("/games")
    fun beginGame(
        @Body coordinateDto: CoordinateDto,
    ): Call<AdventureDto>

    @PATCH("/games/{gameId}")
    fun endGame(
        @Path("gameId") gameId: Long,
        @Body finishGameDto: FinishGameDto,
    ): Call<AdventureStatusDto>

    @GET("/games/{gameId}/result")
    fun getGameResult(
        @Path("gameId") gameId: Long,
    ): Call<AdventureResultDto>

    @GET("/games/results")
    fun getMyGameResults(
        @Query("sort-by") sortBy: String,
        @Query("order") order: String,
    ): Call<List<AdventureResultDto>>

    @GET("/games/{gameId}/hints/{hintId}")
    fun getHint(
        @Path("gameId") gameId: Long,
        @Path("hintId") hingId: Long,
    ): Call<HintDto>

    @POST("/games/{gameId}/hints")
    fun requestHint(
        @Path("gameId") gameId: Long,
        @Body coordinateDto: CoordinateDto,
    ): Call<HintDto>
}
