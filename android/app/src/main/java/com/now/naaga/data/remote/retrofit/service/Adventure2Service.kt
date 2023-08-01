package com.now.naaga.data.remote.retrofit.service

import com.now.naaga.data.remote.dto.CoordinateDto
import com.now.naaga.data.remote.dto.third.AdventureResultDto
import com.now.naaga.data.remote.dto.third.AdventureStatusDto
import com.now.naaga.data.remote.dto.third.FinishGameDto
import com.now.naaga.data.remote.dto.third.GameDto
import com.now.naaga.data.remote.dto.third.HintDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 3차 데모데이용
interface Adventure2Service {
    @GET("/games")
    fun getMyGames(): Call<List<GameDto>>

    @GET("/games/{gameId}")
    fun getGame(
        @Path("gameId") gameId: Long,
    ): Call<GameDto>

    @GET("/games")
    fun getGamesByStatus(
        @Query("status") status: String,
    ): Call<List<GameDto>>

    @POST("/games")
    fun beginGame(
        @Body coordinateDto: CoordinateDto,
    ): Call<GameDto>

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
