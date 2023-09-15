package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.AdventureResultDto
import com.now.naaga.data.remote.dto.AdventureStatusDto
import com.now.naaga.data.remote.dto.CoordinateDto
import com.now.naaga.data.remote.dto.FinishGameDto
import com.now.naaga.data.remote.dto.HintDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdventureService {
    @GET("/games")
    suspend fun getMyGames(): Response<List<AdventureDto>>

    @GET("/games/{gameId}")
    suspend fun getGame(
        @Path("gameId") gameId: Long,
    ): Response<AdventureDto>

    @GET("/games")
    suspend fun getGamesByStatus(
        @Query("status") status: String,
    ): Response<List<AdventureDto>>

    @POST("/games")
    suspend fun beginGame(
        @Body coordinateDto: CoordinateDto,
    ): Response<AdventureDto>

    @PATCH("/games/{gameId}")
    suspend fun endGame(
        @Path("gameId") gameId: Long,
        @Body finishGameDto: FinishGameDto,
    ): Response<AdventureStatusDto>

    @GET("/games/{gameId}/result")
    suspend fun getGameResult(
        @Path("gameId") gameId: Long,
    ): Response<AdventureResultDto>

    @GET("/games/results")
    suspend fun getMyGameResults(
        @Query("sort-by") sortBy: String,
        @Query("order") order: String,
    ): Response<List<AdventureResultDto>>

    @GET("/games/{gameId}/hints/{hintId}")
    suspend fun getHint(
        @Path("gameId") gameId: Long,
        @Path("hintId") hingId: Long,
    ): Response<HintDto>

    @POST("/games/{gameId}/hints")
    suspend fun requestHint(
        @Path("gameId") gameId: Long,
        @Body coordinateDto: CoordinateDto,
    ): Response<HintDto>
}
