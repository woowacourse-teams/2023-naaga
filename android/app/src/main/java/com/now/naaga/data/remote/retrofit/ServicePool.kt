package com.now.naaga.data.remote.retrofit

import com.now.naaga.data.remote.retrofit.RetrofitFactory.retrofit
import com.now.naaga.data.remote.retrofit.service.AdventureService
import com.now.naaga.data.remote.retrofit.service.AuthService
import com.now.naaga.data.remote.retrofit.service.PlaceService
import com.now.naaga.data.remote.retrofit.service.RankService
import com.now.naaga.data.remote.retrofit.service.StatisticsService

object ServicePool {
    val adventureService = retrofit.create(AdventureService::class.java)
    val rankService = retrofit.create(RankService::class.java)
    val statisticsService = retrofit.create(StatisticsService::class.java)
    val placeService = retrofit.create(PlaceService::class.java)
    val authService = retrofit.create(AuthService::class.java)
}
